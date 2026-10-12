package me.whereareiam.identica.engine.pipeline.scenario;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.identica.event.EventListener;
import me.whereareiam.identica.event.EventManager;
import me.whereareiam.identica.event.base.IdenticEvent;
import me.whereareiam.identica.event.identity.IdentityDetachedEvent;
import me.whereareiam.identica.event.scenario.ScenarioRequiredEvent;
import me.whereareiam.identica.event.scenario.ScenarioResolvedEvent;
import me.whereareiam.identica.model.auth.AuthContext;
import me.whereareiam.identica.model.migration.MigrationContext;
import me.whereareiam.identica.model.registration.RegistrationContext;
import me.whereareiam.identica.pipeline.PendingScenarioService;
import me.whereareiam.identica.pipeline.ScenarioContext;
import me.whereareiam.identica.type.event.EventOrder;
import me.whereareiam.identica.type.pipeline.PipelineType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Follows the scenario events of the connections on this proxy. It listens first, so other listeners of the
 * same event already see the connection's new state.
 */
@Singleton
public class DefaultPendingScenarioService implements PendingScenarioService, EventListener {
	private final Map<UUID, PipelineType> pendingByConnection = new ConcurrentHashMap<>();

	@Inject
	public DefaultPendingScenarioService(@NotNull EventManager eventManager) {
		eventManager.register(this);
	}

	@Override
	public @NotNull Optional<PipelineType> find(@NotNull UUID connectionUniqueId) {
		return Optional.ofNullable(pendingByConnection.get(connectionUniqueId));
	}

	@IdenticEvent(EventOrder.LOWEST)
	public void onScenarioRequired(@NotNull ScenarioRequiredEvent event) {
		PipelineType type = resolvePipelineType(event.getContext());
		if (type == null) return;

		pendingByConnection.put(event.getConnectionUniqueId(), type);
	}

	@IdenticEvent(EventOrder.LOWEST)
	public void onScenarioResolved(@NotNull ScenarioResolvedEvent event) {
		PipelineType type = resolvePipelineType(event.getContext());
		if (type == null) return;

		// a scenario that follows, such as a migration after authentication, may already be pending
		pendingByConnection.remove(event.getConnectionUniqueId(), type);
	}

	@IdenticEvent(EventOrder.LOWEST)
	public void onIdentityDetached(@NotNull IdentityDetachedEvent event) {
		pendingByConnection.remove(event.getUniqueId());
	}

	private static @Nullable PipelineType resolvePipelineType(@NotNull ScenarioContext context) {
		return switch (context) {
			case AuthContext ignored -> PipelineType.AUTHENTICATION;
			case RegistrationContext ignored -> PipelineType.REGISTRATION;
			case MigrationContext ignored -> PipelineType.MIGRATION;
			default -> null;
		};
	}
}
