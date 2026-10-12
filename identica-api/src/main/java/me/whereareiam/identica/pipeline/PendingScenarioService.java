package me.whereareiam.identica.pipeline;

import me.whereareiam.identica.type.pipeline.PipelineType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Tells which scenario, if any, a connection on this proxy still has to complete: authentication,
 * registration or migration.
 *
 * <p>A scenario is pending from its {@link me.whereareiam.identica.event.scenario.ScenarioRequiredEvent} until
 * its {@link me.whereareiam.identica.event.scenario.ScenarioResolvedEvent}, or until the connection leaves.
 * Addons that hold back chat or commands until a player has authenticated ask here instead of tracking those
 * events themselves.</p>
 */
public interface PendingScenarioService {
	/**
	 * Finds the scenario pending for a connection.
	 *
	 * @param connectionUniqueId connection unique id
	 * @return pending scenario type, or empty when the connection has nothing left to complete
	 */
	@NotNull Optional<PipelineType> find(@NotNull UUID connectionUniqueId);

	/**
	 * Returns whether a scenario is pending for a connection.
	 *
	 * @param connectionUniqueId connection unique id
	 * @return {@code true} while the connection still has to authenticate, register or migrate
	 */
	default boolean isPending(@NotNull UUID connectionUniqueId) {
		return find(connectionUniqueId).isPresent();
	}
}
