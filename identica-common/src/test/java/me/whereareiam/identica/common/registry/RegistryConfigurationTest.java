package me.whereareiam.identica.common.registry;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("Registry Configuration")
class RegistryConfigurationTest {
	private static final Key<Registry<Reloadable>> REGISTRY = Key.get(new TypeLiteral<>() {});

	@DisplayName("The core and every provider injector share one registry of reloadable components")
	@Test
	void oneRegistryForTheCoreAndItsProviders() {
		Injector core = Guice.createInjector(new RegistryConfiguration());
		Injector provider = core.createChildInjector();

		assertSame(core.getInstance(REGISTRY), core.getInstance(ReloadableRegistry.class));
		assertSame(core.getInstance(REGISTRY), provider.getInstance(REGISTRY));
	}

	@DisplayName("A component registered after startup is reloaded, and one unregistered is not")
	@Test
	void registrationsAfterStartupAreSeen() {
		Injector core = Guice.createInjector(new RegistryConfiguration());
		Registry<Reloadable> registry = core.getInstance(REGISTRY);
		Reloadable kept = () -> {};
		Reloadable removed = () -> {};

		core.createChildInjector().getInstance(REGISTRY).register(kept);
		registry.register(removed);
		registry.unregister(removed);

		assertEquals(Set.of(kept), core.getInstance(REGISTRY).values());
	}
}
