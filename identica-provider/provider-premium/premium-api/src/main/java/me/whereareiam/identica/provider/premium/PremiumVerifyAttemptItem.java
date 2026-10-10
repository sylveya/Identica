package me.whereareiam.identica.provider.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.whereareiam.identica.pipeline.state.StateKey;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class PremiumVerifyAttemptItem {
	/**
	 * Key of the premium verification attempt in a pipeline state. Saved with a pending pipeline.
	 */
	public static final StateKey<PremiumVerifyAttemptItem> KEY =
			StateKey.persisted("premium:verify-attempt", PremiumVerifyAttemptItem.class);

	private long attemptedAt;
}
