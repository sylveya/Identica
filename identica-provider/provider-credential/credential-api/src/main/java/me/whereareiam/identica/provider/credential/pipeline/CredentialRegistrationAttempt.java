package me.whereareiam.identica.provider.credential.pipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.identica.pipeline.state.StateKey;

/**
 * A password a player typed to register, or to confirm the one they typed before. It lives only in the run the
 * command starts and is never saved with the pipeline state.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialRegistrationAttempt {
	/**
	 * Key of the password typed for a registration in a pipeline state. Only lives during one run and is never saved.
	 */
	public static final StateKey<CredentialRegistrationAttempt> KEY =
			StateKey.run("credential:registration-attempt", CredentialRegistrationAttempt.class);

	private String password;
	private boolean confirm;
}
