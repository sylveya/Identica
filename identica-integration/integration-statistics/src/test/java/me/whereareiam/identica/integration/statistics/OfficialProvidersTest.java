package me.whereareiam.identica.integration.statistics;

import me.whereareiam.identica.model.provider.InternalProvider;
import me.whereareiam.identica.model.provider.ProviderDescriptor;
import me.whereareiam.identica.provider.ProviderManager;
import me.whereareiam.identica.type.provider.ProviderState;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfficialProvidersTest {
	private final ClassLoader credentialLoader = new URLClassLoader(new URL[0], null);
	private final ClassLoader customLoader = new URLClassLoader(new URL[0], null);

	@Test
	void namesOnlyIdenticasProviders() {
		OfficialProviders providers = providers();

		assertEquals("credential", providers.statisticsId(provider("credential", "me.whereareiam.identica.provider.credential.Main", credentialLoader)));
		assertEquals("custom", providers.statisticsId(provider("discord", "com.example.Discord", customLoader)));
		assertEquals("custom", providers.statisticsId(provider("premium", "com.example.FakePremium", customLoader)));
	}

	@Test
	void namesStoredIdsOfIdenticasProviders() {
		OfficialProviders providers = providers();

		assertEquals("premium", providers.statisticsId(" Premium "));
		assertEquals("custom", providers.statisticsId("discord"));
	}

	@Test
	void ownsOnlyTheClassLoadersOfIdenticasRunningProviders() {
		assertEquals(List.of(credentialLoader), providers().classLoaders());
	}

	private OfficialProviders providers() {
		ProviderManager manager = mock(ProviderManager.class);
		when(manager.getProviders()).thenReturn(List.of(
				provider("credential", "me.whereareiam.identica.provider.credential.Main", credentialLoader),
				provider("discord", "com.example.Discord", customLoader)
		));

		return new OfficialProviders(manager);
	}

	private static InternalProvider provider(String id, String main, ClassLoader loader) {
		ProviderDescriptor descriptor = new ProviderDescriptor();
		descriptor.setId(id);
		descriptor.setMain(main);

		return InternalProvider.builder()
				.descriptor(descriptor)
				.classLoader(loader)
				.state(ProviderState.ENABLED)
				.build();
	}
}
