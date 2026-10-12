package me.whereareiam.identica.annotation;

import java.lang.annotation.*;

/**
 * Marks a command that a player uses while a scenario is pending for their connection, before they are
 * authenticated: logging in, registering, choosing a provider or confirming a verification code.
 *
 * <p>Put it on the command method next to its {@link Definition}. Addons that hold back other commands until a
 * player has authenticated read these commands through
 * {@link me.whereareiam.identica.command.CommandService#getCommandsAvailableWhilePending()}.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AvailableWhilePending {
}
