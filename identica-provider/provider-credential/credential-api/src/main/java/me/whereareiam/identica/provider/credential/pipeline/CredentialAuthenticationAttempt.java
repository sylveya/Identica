package me.whereareiam.identica.provider.credential.pipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.identica.pipeline.state.StateKey;

/**
 * The password a player typed to sign in. It lives only in the run the command starts and is never saved with
 * the pipeline state.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CredentialAuthenticationAttempt {
	/**
	 * Key of the password typed for a login in a pipeline state. Only lives during one run and is never saved.
	 */
	public static final StateKey<CredentialAuthenticationAttempt> KEY =
			StateKey.run("credential:login-attempt", CredentialAuthenticationAttempt.class);

	private String password;
}
