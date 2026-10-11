package me.whereareiam.identica.logging;

import org.jetbrains.annotations.NotNull;

/**
 * Receives the errors Identica logs together with the exception behind them, such as the error
 * reporting of the statistics integration.
 */
@FunctionalInterface
public interface ErrorListener {
	/**
	 * Called after an error was logged, on the thread that logged it. Must return quickly and never
	 * throw.
	 *
	 * @param error     the exception behind the error
	 * @param message   the log message template, before formatting
	 * @param arguments the values formatted into the template, which may describe a player
	 */
	void logged(@NotNull Throwable error, @NotNull String message, @NotNull Object[] arguments);
}
