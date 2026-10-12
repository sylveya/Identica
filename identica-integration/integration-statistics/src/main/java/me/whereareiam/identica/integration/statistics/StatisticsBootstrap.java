package me.whereareiam.identica.integration.statistics;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.identica.database.schema.SchemaInstaller;
import me.whereareiam.identica.event.EventListener;
import me.whereareiam.identica.event.EventManager;
import me.whereareiam.identica.event.base.IdenticEvent;
import me.whereareiam.identica.event.lifecycle.IdenticaReadyEvent;
import me.whereareiam.identica.event.lifecycle.IdenticaShutdownEvent;
import me.whereareiam.identica.integration.statistics.database.StatisticsLeaseEntity;
import me.whereareiam.identica.integration.statistics.database.StatisticsSchema;
import me.whereareiam.identica.integration.statistics.error.LoggedErrors;
import me.whereareiam.identica.integration.statistics.error.StatisticsErrors;
import me.whereareiam.identica.integration.statistics.metric.StatisticsMetrics;
import me.whereareiam.identica.logging.Logger;
import me.whereareiam.identica.type.event.EventOrder;
import me.whereareiam.statistica.Statistica;
import me.whereareiam.statistica.adapter.jdbc.JdbcElection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;

/**
 * Starts Identica's statistics once Identica is ready, and stops them before the database closes.
 * <p>
 * Statistics never keep Identica from running: when they cannot start, Identica logs why and
 * carries on without them.
 */
@Singleton
public final class StatisticsBootstrap implements EventListener {
	/** Identica's FastStats project. The token ships with every copy of Identica and is not a secret. */
	static final String FASTSTATS_TOKEN = "8ee4383c91584304940b0bc8ec7233ec";

	private final @NotNull StatisticsPlatform platform;
	private final @NotNull StatisticsMetrics metrics;
	private final @NotNull OfficialProviders officialProviders;
	private final @NotNull SchemaInstaller schemaInstaller;
	private final @NotNull Provider<DataSource> dataSource;
	private @Nullable Statistica statistica;

	@Inject
	public StatisticsBootstrap(
			@NotNull StatisticsPlatform platform,
			@NotNull StatisticsMetrics metrics,
			@NotNull OfficialProviders officialProviders,
			@NotNull SchemaInstaller schemaInstaller,
			@NotNull Provider<DataSource> dataSource,
			@NotNull EventManager eventManager
	) {
		this.platform = platform;
		this.metrics = metrics;
		this.officialProviders = officialProviders;
		this.schemaInstaller = schemaInstaller;
		this.dataSource = dataSource;
		eventManager.register(this);
	}

	@IdenticEvent(EventOrder.NORMAL)
	public synchronized void onReady(@NotNull IdenticaReadyEvent event) {
		if (statistica != null) return;

		try {
			schemaInstaller.install(new StatisticsSchema());

			Statistica statistica = platform.builder()
					.faststats(FASTSTATS_TOKEN)
					.election(JdbcElection.of(dataSource.get(), StatisticsLeaseEntity.TABLE))
					.metrics(metrics.create())
					.errors(StatisticsErrors.policy(officialProviders))
					.build();
			statistica.start();

			Logger.setErrorListener(new LoggedErrors(statistica.errors()));
			this.statistica = statistica;
		} catch (RuntimeException | LinkageError failure) {
			Logger.warn("Usage statistics are off: %s", String.valueOf(failure.getMessage()));
		}
	}

	/**
	 * Runs before Identica stops its providers and closes the database, so the last submission
	 * still describes the running network and the lease is given up.
	 */
	@IdenticEvent(EventOrder.LOWEST)
	public synchronized void onShutdown(@NotNull IdenticaShutdownEvent event) {
		if (statistica == null) return;

		Logger.setErrorListener(null);
		statistica.stop();
		statistica = null;
	}
}
