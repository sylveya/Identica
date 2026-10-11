package me.whereareiam.identica.identity.username;

import me.whereareiam.identica.model.identity.username.UsernameConflictResult;
import me.whereareiam.identica.model.identity.username.UsernameConflictSubject;
import org.jetbrains.annotations.NotNull;

/**
 * Detects and resolves a conflict between a username a connection wants to use and the accounts
 * that already hold it, applying the effects of the resolution to existing sessions.
 */
public interface UsernameConflictHandler {
	/**
	 * Checks the subject for a username conflict and resolves it through the configured conflict rules.
	 *
	 * @param subject identity and provider information of the incoming connection
	 * @param currentEffectiveUsername username the connection would use without a conflict
	 * @return allowed outcome with the username to apply, or a denial with its message
	 */
	@NotNull UsernameConflictResult handle(@NotNull UsernameConflictSubject subject, @NotNull String currentEffectiveUsername);
}
