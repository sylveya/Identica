package me.whereareiam.identica;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@DisplayName("Identica API")
class IdenticaAPITest {
	private final IdenticaAPI identica = mock(IdenticaAPI.class);

	@AfterEach
	void unregister() {
		IdenticaAPI.unregister();
	}

	@DisplayName("Without a running instance, get throws and find is empty")
	@Test
	void notRunning() {
		assertThrows(IllegalStateException.class, IdenticaAPI::get);
		assertEquals(Optional.empty(), IdenticaAPI.find());
	}

	@DisplayName("Waiting actions run once Identica is ready, not when it registers; later ones run immediately")
	@Test
	void whenReadyWaitsForTheProvidersToLoad() {
		List<String> ran = new ArrayList<>();
		IdenticaAPI.whenReady(instance -> ran.add("before"));

		IdenticaAPI.register(identica);
		IdenticaAPI.whenReady(instance -> ran.add("registered"));
		assertEquals(List.of(), ran);
		assertSame(identica, IdenticaAPI.get());

		IdenticaAPI.markReady();
		IdenticaAPI.whenReady(instance -> ran.add("after"));

		assertEquals(List.of("before", "registered", "after"), ran);
	}

	@DisplayName("A restarted Identica waits for its providers again")
	@Test
	void restartWaitsAgain() {
		IdenticaAPI.register(identica);
		IdenticaAPI.markReady();
		IdenticaAPI.unregister();

		List<String> ran = new ArrayList<>();
		IdenticaAPI.register(identica);
		IdenticaAPI.whenReady(instance -> ran.add("restarted"));
		assertEquals(List.of(), ran);

		IdenticaAPI.markReady();
		assertEquals(List.of("restarted"), ran);
	}

	@DisplayName("A failing action does not keep the others or the registration from completing")
	@Test
	void failingActionDoesNotStopOthers() {
		List<String> ran = new ArrayList<>();
		IdenticaAPI.whenReady(instance -> {
			throw new IllegalStateException("addon failed");
		});
		IdenticaAPI.whenReady(instance -> ran.add("second"));

		IdenticaAPI.register(identica);
		IdenticaAPI.markReady();

		assertEquals(List.of("second"), ran);
		assertSame(identica, IdenticaAPI.get());
	}

	@DisplayName("A second registration is refused until the first instance leaves")
	@Test
	void oneInstanceAtATime() {
		IdenticaAPI.register(identica);

		assertThrows(IllegalStateException.class, () -> IdenticaAPI.register(mock(IdenticaAPI.class)));

		IdenticaAPI.unregister();
		assertEquals(Optional.empty(), IdenticaAPI.find());
	}
}
