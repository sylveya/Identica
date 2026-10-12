package me.whereareiam.identica.event.lifecycle;

import me.whereareiam.identica.event.base.SynchronousEvent;

/**
 * Event called after {@code /identica admin reload} has reloaded every registered component.
 *
 * <p>Configuration documents are new objects after a reload. Addons that keep values derived from them, such
 * as the messages read through {@link me.whereareiam.identica.config.ConfigCatalog}, rebuild them here. A
 * component that failed to reload keeps its previous state, and the event is still called.</p>
 */
public class IdenticaReloadedEvent implements SynchronousEvent {
}
