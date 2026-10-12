package me.whereareiam.identica.model.config;

/**
 * Marks a configuration document that holds player-facing messages.
 *
 * <p>Core, providers and features each keep their own messages document; every one of them implements this
 * interface, so an addon finds them all through {@link me.whereareiam.identica.config.ConfigCatalog} without
 * knowing their types:</p>
 *
 * <pre>{@code
 * Collection<MessageDocument> messages = identica.getConfigCatalog().documents(MessageDocument.class);
 * }</pre>
 */
public interface MessageDocument {
}
