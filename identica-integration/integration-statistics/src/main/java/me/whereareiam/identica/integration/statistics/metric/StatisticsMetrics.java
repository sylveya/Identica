package me.whereareiam.identica.integration.statistics.metric;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.identica.persistence.AccountPersistenceService;
import me.whereareiam.identica.persistence.provider.ProviderLinkPersistenceService;
import me.whereareiam.identica.feature.FeatureRegistry;
import me.whereareiam.identica.feature.verification.VerificationMethod;
import me.whereareiam.identica.feature.verification.VerificationRegistry;
import me.whereareiam.identica.feature.verification.database.VerificationPersistenceService;
import me.whereareiam.identica.feature.verification.model.VerificationMethodDescriptor;
import me.whereareiam.identica.integration.statistics.OfficialProviders;
import me.whereareiam.identica.integration.statistics.StatisticsPlatform;
import me.whereareiam.identica.model.config.Replication;
import me.whereareiam.identica.model.config.Settings;
import me.whereareiam.identica.model.config.persistence.Persistence;
import me.whereareiam.identica.model.provider.InternalProvider;
import me.whereareiam.identica.provider.ProviderManager;
import me.whereareiam.identica.type.provider.ProviderState;
import me.whereareiam.statistica.metric.MetricSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;

/**
 * Declares what Identica sends: how a network is set up, how many accounts it stores and how many
 * use verification, what its players did since the last submission, and which Minecraft versions the
 * players online use.
 * <p>
 * Only values Identica itself defines are sent: its provider, feature and verification method ids,
 * with third-party ones counted as {@code custom}, and its configuration's enum constants. No
 * player, server or host name ever leaves the proxy. Account counts are read from the database at
 * most every ten minutes, and only by the proxy elected to send them, because every proxy of a
 * cluster would read the same numbers.
 */
@Singleton
public final class StatisticsMetrics {
	private static final Duration COUNT_INTERVAL = Duration.ofMinutes(10);

	private final @NotNull ProviderManager providerManager;
	private final @NotNull OfficialProviders officialProviders;
	private final @NotNull FeatureRegistry featureRegistry;
	private final @NotNull VerificationRegistry verificationRegistry;
	private final @NotNull Provider<Persistence> persistence;
	private final @NotNull Provider<Replication> replication;
	private final @NotNull Provider<Settings> settings;
	private final @NotNull ActivityCounters activity;
	private final @NotNull Cached<Long> accounts;
	private final @NotNull Cached<Map<String, Long>> providerAccounts;
	private final @NotNull Cached<Long> verifiedAccounts;
	private final @NotNull StatisticsPlatform platform;

	@Inject
	public StatisticsMetrics(
			@NotNull ProviderManager providerManager,
			@NotNull OfficialProviders officialProviders,
			@NotNull FeatureRegistry featureRegistry,
			@NotNull VerificationRegistry verificationRegistry,
			@NotNull Provider<Persistence> persistence,
			@NotNull Provider<Replication> replication,
			@NotNull Provider<Settings> settings,
			@NotNull ActivityCounters activity,
			@NotNull AccountPersistenceService accountPersistence,
			@NotNull ProviderLinkPersistenceService providerLinkPersistence,
			@NotNull VerificationPersistenceService verificationPersistence,
			@NotNull StatisticsPlatform platform
	) {
		this.providerManager = providerManager;
		this.officialProviders = officialProviders;
		this.featureRegistry = featureRegistry;
		this.verificationRegistry = verificationRegistry;
		this.persistence = persistence;
		this.replication = replication;
		this.settings = settings;
		this.activity = activity;
		this.accounts = new Cached<>(accountPersistence::count);
		this.providerAccounts = new Cached<>(() -> byStatisticsId(providerLinkPersistence.countByProvider()));
		this.verifiedAccounts = new Cached<>(verificationPersistence::countEnrolledAccounts);
		this.platform = platform;
	}

	/**
	 * Returns the metrics Identica sends.
	 *
	 * @return the metrics
	 */
	public @NotNull MetricSet create() {
		return MetricSet.builder()
				.stringList("providers", this::providers)
				.stringList("features", this::features)
				.stringList("verification_methods", this::verificationMethods)
				.string("persistence_type", () -> persistence.get().getType().name())
				.string("replication_type", () -> replication.get().isEnabled() ? "REDIS" : "DISABLED")
				.string("unique_id_mode", () -> settings.get().getIdentity().getUniqueIdMode().name())
				.string("session_concurrency", () -> settings.get().getSessions().getConcurrencyPolicy().name())
				.numberMap("authentications", activity.getAuthentications())
				.numberMap("logins_by_provider", activity.getLoginsByProvider())
				.numberMap("registrations", activity.getRegistrations())
				.numberMap("migrations", activity.getMigrations())
				.number("sessions_opened", activity.getSessions())
				.numberMap("client_versions", platform::clientVersions)
				.total("account_count", accounts)
				.totalMap("provider_usage", providerAccounts)
				.total("verified_accounts", verifiedAccounts)
				.build();
	}

	private @NotNull List<String> providers() {
		return enabledProviders().stream()
				.map(officialProviders::statisticsId)
				.distinct()
				.toList();
	}

	private @NotNull List<String> features() {
		Set<String> features = new LinkedHashSet<>();
		for (InternalProvider provider : enabledProviders()) {
			String providerId = provider.getDescriptor() == null ? null : provider.getDescriptor().getId();
			if (providerId == null) continue;

			for (String featureId : featureRegistry.ids())
				if (featureRegistry.isEnabled(providerId, featureId)) features.add(featureId);
		}

		return List.copyOf(features);
	}

	private @NotNull List<String> verificationMethods() {
		Set<String> methods = new LinkedHashSet<>();
		for (VerificationMethod method : verificationRegistry.values()) {
			VerificationMethodDescriptor descriptor = method.descriptor();
			methods.add(descriptor.isBuiltIn() ? descriptor.getId().toLowerCase(Locale.ROOT) : OfficialProviders.CUSTOM);
		}

		return List.copyOf(methods);
	}

	private @NotNull Collection<InternalProvider> enabledProviders() {
		return providerManager.getProviders().stream()
				.filter(provider -> provider.getState() == ProviderState.ENABLED)
				.toList();
	}

	private @NotNull Map<String, Long> byStatisticsId(@NotNull Map<String, Long> counts) {
		Map<String, Long> grouped = new TreeMap<>();
		counts.forEach((providerId, count) -> {
			if (providerId != null && count != null && count > 0)
				grouped.merge(officialProviders.statisticsId(providerId), count, Long::sum);
		});

		return grouped;
	}

	/**
	 * Reuses a value read from the database for {@link #COUNT_INTERVAL}, so both services and the
	 * error reports sent in between share one query.
	 */
	private static final class Cached<T> implements Callable<T> {
		private final @NotNull Callable<T> source;
		private @Nullable T value;
		private long readAt;

		private Cached(@NotNull Callable<T> source) {
			this.source = source;
		}

		@Override
		public synchronized @Nullable T call() throws Exception {
			long now = System.nanoTime();
			if (value != null && now - readAt < COUNT_INTERVAL.toNanos()) return value;

			value = source.call();
			readAt = now;

			return value;
		}
	}
}
