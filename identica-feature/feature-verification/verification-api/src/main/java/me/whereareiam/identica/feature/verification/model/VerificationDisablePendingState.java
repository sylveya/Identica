package me.whereareiam.identica.feature.verification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.identica.pipeline.state.StateKey;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class VerificationDisablePendingState {
	/**
	 * Key of the pending removal of a verification method in a pipeline state. Saved with a pending pipeline.
	 */
	public static final StateKey<VerificationDisablePendingState> KEY =
			StateKey.persisted("verification:disable-pending", VerificationDisablePendingState.class);

	private @NotNull String methodId;
	private long requestedAt;
}
