package me.whereareiam.identica.config;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The configuration documents loaded in the running proxy: core's, the features', and those of every loaded
 * provider, including documents bound in a provider's own injector.
 *
 * <p>Each call returns the current models. A document reloaded by {@code /identica admin reload} is a new
 * object, so read the catalog again after
 * {@link me.whereareiam.identica.event.lifecycle.IdenticaReloadedEvent} instead of keeping the models.</p>
 */
public interface ConfigCatalog {
	/**
	 * Returns the current model of every loaded document, in the order the documents are loaded in.
	 *
	 * @return loaded documents
	 */
	@NotNull Collection<Object> documents();

	/**
	 * Returns the loaded documents that are instances of the given type, such as every
	 * {@link me.whereareiam.identica.model.config.MessageDocument}.
	 *
	 * @param type document type or marker interface
	 * @param <T> document type
	 * @return matching documents, in the order the documents are loaded in
	 */
	@NotNull <T> Collection<T> documents(@NotNull Class<T> type);
}
