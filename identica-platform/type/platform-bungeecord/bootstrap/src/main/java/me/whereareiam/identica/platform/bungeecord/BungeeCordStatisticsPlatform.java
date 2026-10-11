package me.whereareiam.identica.platform.bungeecord;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.identica.integration.statistics.StatisticsPlatform;
import me.whereareiam.statistica.common.StatisticaBuilder;
import me.whereareiam.statistica.platform.bungeecord.BungeeCordStatistica;
import org.jetbrains.annotations.NotNull;

@Singleton
public final class BungeeCordStatisticsPlatform implements StatisticsPlatform {
	private static final int BSTATS_ID = 31292;

	private final @NotNull BungeeCordIdentica plugin;

	@Inject
	public BungeeCordStatisticsPlatform(@NotNull BungeeCordIdentica plugin) {
		this.plugin = plugin;
	}

	@Override
	public @NotNull StatisticaBuilder<?> builder() {
		return BungeeCordStatistica.builder(plugin).bstats(BSTATS_ID);
	}
}
