package me.whereareiam.identica.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest {
	private final List<String> lines = new ArrayList<>();

	@AfterEach
	void reset() {
		Logger.init(null);
		Logger.setErrorListener(null);
	}

	@Test
	void logsTheCauseAndPassesTheErrorOn() {
		List<Object[]> passed = new ArrayList<>();
		IllegalStateException error = new IllegalStateException("broken");
		Logger.init(recording());
		Logger.setErrorListener((failure, message, arguments) -> {
			assertSame(error, failure);
			assertEquals("Step %s failed", message);
			passed.add(arguments);
		});

		Logger.severe(error, "Step %s failed", "login");

		assertTrue(lines.get(0).startsWith("severe Step login failed: broken"));
		assertTrue(lines.get(0).contains("IllegalStateException"));
		assertArrayEquals(new Object[]{"login"}, passed.get(0));
	}

	@Test
	void keepsTheStackTraceOfWarningsForDebugging() {
		Logger.init(recording());

		Logger.warn(new IllegalStateException("broken"), "Step %s failed", "login");

		assertEquals("warn Step login failed: broken", lines.get(0));
		assertTrue(lines.get(1).startsWith("debug java.lang.IllegalStateException: broken"));
	}

	@Test
	void survivesAFailingListener() {
		Logger.init(recording());
		Logger.setErrorListener((failure, message, arguments) -> {
			throw new IllegalStateException("listener");
		});

		Logger.warn(new IllegalStateException("broken"), "Step failed");

		assertTrue(lines.stream().anyMatch(line -> line.startsWith("debug Failed to pass an error on")));
	}

	private LoggingHelper recording() {
		return new LoggingHelper() {
			@Override
			public void info(String message, Object... objects) {
				lines.add("info " + String.format(message, objects));
			}

			@Override
			public void warn(String message, Object... objects) {
				lines.add("warn " + String.format(message, objects));
			}

			@Override
			public void severe(String message, Object... objects) {
				lines.add("severe " + String.format(message, objects));
			}

			@Override
			public void debug(String message, Object... objects) {
				lines.add("debug " + String.format(message, objects));
			}

			@Override
			public void trace(String message, Object... objects) {
				lines.add("trace " + String.format(message, objects));
			}
		};
	}
}
