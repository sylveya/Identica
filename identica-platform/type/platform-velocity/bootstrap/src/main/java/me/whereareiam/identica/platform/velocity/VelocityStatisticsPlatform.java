package me.whereareiam.identica.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import me.whereareiam.identica.integration.statistics.StatisticsPlatform;
import me.whereareiam.statistica.common.StatisticaBuilder;
import me.whereareiam.statistica.platform.velocity.VelocityStatistica;
import org.bstats.velocity.Metrics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;

@Singleton
public final class VelocityStatisticsPlatform implements StatisticsPlatform {
	private static final int BSTATS_ID = 30854;

	private final @NotNull VelocityIdentica plugin;
	private final @NotNull ProxyServer server;
	private final @NotNull Logger logger;
	private final @NotNull Path dataPath;
	private final @NotNull Metrics.Factory bstats;

	@Inject
	public VelocityStatisticsPlatform(
			@NotNull VelocityIdentica plugin,
			@NotNull ProxyServer server,
			@NotNull Logger logger,
			@NotNull Path dataPath,
			@NotNull Metrics.Factory bstats
	) {
		this.plugin = plugin;
		this.server = server;
		this.logger = logger;
		this.dataPath = dataPath;
		this.bstats = bstats;
	}

	@Override
	public @NotNull StatisticaBuilder<?> builder() {
		return VelocityStatistica.builder(plugin, server, logger, dataPath).bstats(bstats, BSTATS_ID);
	}
}
