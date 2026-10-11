package me.whereareiam.identica.integration.statistics;

import me.whereareiam.statistica.common.StatisticaBuilder;
import org.jetbrains.annotations.NotNull;

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
}
