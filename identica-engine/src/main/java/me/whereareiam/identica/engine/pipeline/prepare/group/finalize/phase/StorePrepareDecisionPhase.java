package me.whereareiam.identica.engine.pipeline.prepare.group.finalize.phase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.identica.model.pipeline.phase.PhaseResult;
import me.whereareiam.identica.model.pipeline.prepare.PrepareAccountCandidateItem;
import me.whereareiam.identica.model.pipeline.prepare.PrepareContextItem;
import me.whereareiam.identica.model.pipeline.prepare.decision.PrepareDecision;
import me.whereareiam.identica.pipeline.PipelinePhase;
import me.whereareiam.identica.pipeline.prepare.PrepareStateStore;
import me.whereareiam.identica.pipeline.state.PipelineState;
import me.whereareiam.identica.pipeline.state.prepare.PrepareGroupState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StorePrepareDecisionPhase implements PipelinePhase<PrepareGroupState> {
	private final PrepareStateStore prepareStateStore;

	@Override
	public @NotNull String id() {
		return "store-prepare-decision";
	}

	@Override
	public int order() {
		return 100;
	}

	@Override
	public @NotNull Class<PrepareGroupState> stateType() {
		return PrepareGroupState.class;
	}

	@Override
	public @NotNull CompletionStage<PhaseResult<PrepareGroupState>> execute(
			@NotNull PipelineState pipelineState,
			@NotNull PrepareGroupState state
	) {
		var request = state.getRequest();
		if (request == null)
			return CompletableFuture.completedFuture(PhaseResult.pass(state));

		PrepareDecision decision = pipelineState.get(PrepareDecision.KEY).orElseGet(() -> {
			PrepareContextItem context = pipelineState.get(PrepareContextItem.KEY).orElse(new PrepareContextItem());
			PrepareAccountCandidateItem candidate = pipelineState.get(PrepareAccountCandidateItem.KEY).orElse(null);
			String username = request.getIdentity().getUsername();
			PrepareDecision allowed = PrepareDecision.allow(
					context,
					candidate != null ? candidate.getUniqueId() : null,
					candidate != null && candidate.getEffectiveUsername() != null
							? candidate.getEffectiveUsername()
							: username
			);
			pipelineState.put(PrepareDecision.KEY, allowed);

			return allowed;
		});

		String connectionKey = request.getConnectionKey();
		if (connectionKey != null && !connectionKey.isBlank())
			prepareStateStore.put(connectionKey, decision);

		UUID uniqueId = decision.getAccountUniqueId();
		if (uniqueId != null) prepareStateStore.put(uniqueId, connectionKey, decision);

		return CompletableFuture.completedFuture(PhaseResult.pass(state));
	}
}
