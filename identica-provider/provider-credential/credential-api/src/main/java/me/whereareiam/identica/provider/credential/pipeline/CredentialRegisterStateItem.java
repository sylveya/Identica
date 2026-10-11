package me.whereareiam.identica.provider.credential.pipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.identica.pipeline.state.StateKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRegisterStateItem {
	/**
	 * Key of the password hash waiting for its confirmation in a pipeline state. Saved with a pending pipeline.
	 */
	public static final StateKey<CredentialRegisterStateItem> KEY =
			StateKey.persisted("credential:pending-password", CredentialRegisterStateItem.class);

	private String passwordHash;
	private String hashingMethod;
}
