package me.whereareiam.identica.common.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.common.registry.RegistryConfiguration;
import me.whereareiam.identica.common.registry.ReloadableRegistry;
import me.whereareiam.identica.config.ConfigProvider;
import me.whereareiam.identica.model.config.MessageDocument;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Default Config Catalog")
class DefaultConfigCatalogTest {
	@DisplayName("Lists the documents of the core and of a provider's child injector, and skips other components")
	@Test
	void listsDocumentsOfEveryInjector() {
		Injector core = Guice.createInjector(new RegistryConfiguration());
		ReloadableRegistry registry = core.getInstance(ReloadableRegistry.class);
		Injector provider = core.createChildInjector();
		CoreMessages coreMessages = new CoreMessages();
		Settings settings = new Settings();
		ProviderMessages providerMessages = new ProviderMessages();
		new Document<>("messages", coreMessages, registry);
		new Document<>("settings", settings, registry);
		new Document<>("provider-messages", providerMessages, provider.getInstance(ReloadableRegistry.class));
		registry.register(() -> {});

		DefaultConfigCatalog catalog = new DefaultConfigCatalog(registry);

		assertEquals(3, catalog.documents().size());
		// documents of one class have no load order among each other
		assertEquals(Set.of(coreMessages, providerMessages), Set.copyOf(catalog.documents(MessageDocument.class)));
	}

	@DisplayName("A provider's documents leave the catalog when the provider is unloaded")
	@Test
	void unregisteredDocumentsLeave() {
		ReloadableRegistry registry = new ReloadableRegistry();
		Document<ProviderMessages> document = new Document<>("provider-messages", new ProviderMessages(), registry);

		registry.unregister(document);

		assertEquals(List.of(), List.copyOf(new DefaultConfigCatalog(registry).documents(MessageDocument.class)));
	}

	private static final class CoreMessages implements MessageDocument {
	}

	private static final class ProviderMessages implements MessageDocument {
	}

	private static final class Settings {
	}

	private static final class Document<T> extends ConfigProvider<T> {
		private final @NotNull T value;

		@SuppressWarnings("unchecked")
		private Document(@NotNull String fileName, @NotNull T value, @NotNull Registry<Reloadable> registry) {
			super(Path.of("."), fileName, (Class<? extends T>) value.getClass(), registry);
			this.value = value;
		}

		@Override
		protected @NotNull T load() {
			return value;
		}

		@Override
		protected Configura configura() {
			return Config.configured();
		}
	}
}
