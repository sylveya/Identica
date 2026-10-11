package me.whereareiam.identica.common.registry;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;

public class RegistryConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(new TypeLiteral<Registry<Reloadable>>() {})
				.to(ReloadableRegistry.class)
				.asEagerSingleton();
	}
}
