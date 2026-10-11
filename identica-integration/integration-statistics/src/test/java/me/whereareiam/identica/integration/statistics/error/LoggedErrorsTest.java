package me.whereareiam.identica.integration.statistics.error;

import me.whereareiam.identica.type.ScenarioResolution;
import me.whereareiam.statistica.error.ErrorContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggedErrorsTest {
	@Test
	void sendsTheTemplateAndHidesTheArguments() {
		List<ErrorContext> reports = new ArrayList<>();
		UUID account = UUID.fromString("0b7c2a1e-3f4d-4c5b-9a8e-1f2d3c4b5a69");
		LoggedErrors errors = new LoggedErrors((error, context) -> reports.add(context));

		errors.logged(
				new IllegalStateException(),
				"Prepare failed for %s account=%s attempt=%s result=%s",
				new Object[]{"Alice", account, 3, ScenarioResolution.FAILED}
		);

		assertEquals(Map.of("message", "Prepare failed for %s account=%s attempt=%s result=%s"), reports.get(0).getAttributes());
		assertEquals(Set.of("Alice", account.toString()), reports.get(0).getRedactions());
	}
}
