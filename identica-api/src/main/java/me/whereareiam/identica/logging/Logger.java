package me.whereareiam.identica.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Identica's log, written through the platform's {@link LoggingHelper}.
 */
public final class Logger {
	private static LoggingHelper loggingHelper;
	private static volatile @Nullable ErrorListener errorListener;

	public static void init(LoggingHelper loggingHelper) {
		Logger.loggingHelper = loggingHelper;
	}

	/**
	 * Sets who receives the errors logged with their exception, replacing the previous listener.
	 *
	 * @param listener the listener, or {@code null} to stop passing errors on
	 */
	public static void setErrorListener(@Nullable ErrorListener listener) {
		Logger.errorListener = listener;
	}

	public static void info(String message, Object... objects) {
		if (loggingHelper != null) loggingHelper.info(message, objects);
	}

	public static void warn(String message, Object... objects) {
		if (loggingHelper != null) loggingHelper.warn(message, objects);
	}

	/**
	 * Logs a warning caused by an exception: the message with the exception's own message appended,
	 * and the stack trace at debug level. The exception is passed on to the {@link ErrorListener}.
	 *
	 * @param error   the exception behind the warning
	 * @param message the message template
	 * @param objects values formatted into the template
	 */
	public static void warn(@NotNull Throwable error, @NotNull String message, Object... objects) {
		warn(message + ": %s", append(objects, error));
		debug("%s", stackTrace(error));
		logged(error, message, objects);
	}

	public static void severe(String message, Object... objects) {
		if (loggingHelper != null) loggingHelper.severe(message, objects);
	}

	/**
	 * Logs an error caused by an exception: the message with the exception's own message appended,
	 * followed by the stack trace. The exception is passed on to the {@link ErrorListener}.
	 *
	 * @param error   the exception behind the error
	 * @param message the message template
	 * @param objects values formatted into the template
	 */
	public static void severe(@NotNull Throwable error, @NotNull String message, Object... objects) {
		severe(message + ": %s%n%s", append(append(objects, error), stackTrace(error)));
		logged(error, message, objects);
	}

	public static void debug(String message, Object... objects) {
		if (loggingHelper != null) loggingHelper.debug(message, objects);
	}

	public static void trace(String message, Object... objects) {
		if (loggingHelper != null) loggingHelper.trace(message, objects);
	}

	private static void logged(@NotNull Throwable error, @NotNull String message, Object @Nullable [] objects) {
		ErrorListener listener = errorListener;
		if (listener == null) return;

		try {
			listener.logged(error, message, objects == null ? new Object[0] : objects.clone());
		} catch (RuntimeException | LinkageError failure) {
			debug("Failed to pass an error on: %s", failure);
		}
	}

	private static Object @NotNull [] append(Object @Nullable [] objects, @NotNull Throwable error) {
		return append(objects, error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName());
	}

	private static Object @NotNull [] append(Object @Nullable [] objects, @NotNull Object value) {
		Object[] arguments = objects == null ? new Object[0] : objects;
		Object[] appended = new Object[arguments.length + 1];
		System.arraycopy(arguments, 0, appended, 0, arguments.length);
		appended[arguments.length] = value;

		return appended;
	}

	private static @NotNull String stackTrace(@NotNull Throwable error) {
		StringWriter writer = new StringWriter();
		error.printStackTrace(new PrintWriter(writer));

		return writer.toString();
	}
}
