package me.whereareiam.identica.common.registry;

import com.google.inject.Singleton;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.common.config.ConfigLoadOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Every component of the running proxy that {@code /identica admin reload} refreshes: the core's, the features'
 * and traits', and those of each loaded provider, whose injectors are children of the core's. There is one
 * registry; a provider's components leave it when the provider is unloaded.
 */
@Singleton
public class ReloadableRegistry implements Registry<Reloadable> {
	private final Set<Reloadable> reloadables = ConcurrentHashMap.newKeySet();

	@Override
	public void register(Reloadable value) {
		reloadables.add(value);
	}

	@Override
	public void unregister(Reloadable value) {
		reloadables.remove(value);
	}

	/**
	 * Returns the registered components in the order they are reloaded: documents in the order they are loaded in
	 * at startup, a document before the extensions of it, then the components built from documents.
	 */
	@Override
	public Set<Reloadable> values() {
		List<Reloadable> ordered = new ArrayList<>(reloadables);
		ordered.sort(Comparator.comparing(reloadable -> (Class<?>) reloadable.getClass(), ConfigLoadOrder.BY_TYPE));

		return Collections.unmodifiableSet(new LinkedHashSet<>(ordered));
	}
}
