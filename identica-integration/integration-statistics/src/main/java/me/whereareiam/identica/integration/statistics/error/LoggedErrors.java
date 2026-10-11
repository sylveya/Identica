package me.whereareiam.identica.integration.statistics.error;

import me.whereareiam.identica.logging.ErrorListener;
import me.whereareiam.statistica.error.ErrorContext;
import me.whereareiam.statistica.error.ErrorReporter;
import org.jetbrains.annotations.NotNull;

/**
 * Reports the errors Identica logs with their exception.
 * <p>
 * The log message's template tells where the error happened. Its arguments are never sent: they
 * may name a player, so they are hidden wherever they appear in the exception's messages. Numbers,
 * booleans and enum constants are Identica's own values and stay.
 */
public final class LoggedErrors implements ErrorListener {
	private final @NotNull ErrorReporter reporter;

	public LoggedErrors(@NotNull ErrorReporter reporter) {
		this.reporter = reporter;
	}

	@Override
	public void logged(@NotNull Throwable error, @NotNull String message, @NotNull Object[] arguments) {
		ErrorContext.Builder context = ErrorContext.builder().attribute("message", message);
		for (Object argument : arguments)
			if (!(argument instanceof Number || argument instanceof Boolean || argument instanceof Enum<?>))
				context.redact(argument);

		reporter.report(error, context.build());
	}
}
