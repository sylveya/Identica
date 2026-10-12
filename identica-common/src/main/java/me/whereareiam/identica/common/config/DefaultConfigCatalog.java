package me.whereareiam.identica.common.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.config.ConfigCatalog;
import me.whereareiam.identica.config.ConfigProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Reads the documents from the reloadable registry, where every {@link ConfigProvider} registers itself:
 * core's, the features', and those of each loaded provider, whose injectors are children of the core's. A
 * provider's documents leave the catalog when the provider is unloaded.
 */
@Singleton
public class DefaultConfigCatalog implements ConfigCatalog {
	private final Registry<Reloadable> reloadables;

	@Inject
	public DefaultConfigCatalog(@NotNull Registry<Reloadable> reloadables) {
		this.reloadables = reloadables;
	}

	@Override
	public @NotNull Collection<Object> documents() {
		return documents(Object.class);
	}

	@Override
	public @NotNull <T> Collection<T> documents(@NotNull Class<T> type) {
		List<T> documents = new ArrayList<>();
		for (Reloadable reloadable : reloadables.values()) {
			if (!(reloadable instanceof ConfigProvider<?> provider)) continue;

			Object document = provider.get();
			if (type.isInstance(document)) documents.add(type.cast(document));
		}

		return Collections.unmodifiableList(documents);
	}
}
