package me.whereareiam.identica.integration.statistics;

import com.google.inject.AbstractModule;

/**
 * Turns Identica's statistics on. A platform installs it and binds its {@link StatisticsPlatform}.
 */
public final class StatisticsConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(StatisticsBootstrap.class).asEagerSingleton();
	}
}
