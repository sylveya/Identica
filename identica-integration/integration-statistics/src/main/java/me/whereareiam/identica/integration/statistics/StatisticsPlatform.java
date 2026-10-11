package me.whereareiam.identica.integration.statistics;

import me.whereareiam.statistica.common.StatisticaBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * What a platform provides for Identica's statistics: Statistica's builder of that platform, with
 * the platform's bStats id already set.
 */
public interface StatisticsPlatform {
	/**
	 * Starts the configuration of Identica's statistics on this platform.
	 *
	 * @return a new builder
	 */
	@NotNull StatisticaBuilder<?> builder();

	/**
	 * Counts the players online on this proxy by the Minecraft version of their client, such as
	 * {@code 1.21.4}. Only versions are counted; no player is named.
	 *
	 * @return players by client version
	 */
	@NotNull Map<String, Integer> clientVersions();
}
