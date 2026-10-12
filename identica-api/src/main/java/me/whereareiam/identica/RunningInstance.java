package me.whereareiam.identica;

import me.whereareiam.identica.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Holds the {@link IdenticaAPI} of the running Identica instance behind the static methods of
 * {@link IdenticaAPI}; an interface cannot keep this state itself without exposing it.
 */
final class RunningInstance {
	private static final List<Consumer<IdenticaAPI>> waiting = new ArrayList<>();
	private static volatile IdenticaAPI instance;

	private RunningInstance() {
		throw new UnsupportedOperationException("This class cannot be instantiated");
	}

	static @NotNull IdenticaAPI get() {
		IdenticaAPI current = instance;
		if (current == null)
			throw new IllegalStateException("Identica is not running. Load your addon after Identica, or use IdenticaAPI.whenReady.");

		return current;
	}

	static @NotNull Optional<IdenticaAPI> find() {
		return Optional.ofNullable(instance);
	}

	static void whenReady(@NotNull Consumer<IdenticaAPI> action) {
		IdenticaAPI current;
		synchronized (waiting) {
			current = instance;
			if (current == null) {
				waiting.add(action);
				return;
			}
		}

		action.accept(current);
	}

	static void register(@NotNull IdenticaAPI identica) {
		List<Consumer<IdenticaAPI>> ready;
		synchronized (waiting) {
			if (instance != null) throw new IllegalStateException("Identica is already registered");

			instance = identica;
			ready = List.copyOf(waiting);
			waiting.clear();
		}

		for (Consumer<IdenticaAPI> action : ready) {
			try {
				action.accept(identica);
			} catch (RuntimeException failure) {
				// an addon's failure is not Identica's, so it is not passed on to the error reports
				Logger.warn("An action waiting for Identica failed: %s", failure);
			}
		}
	}

	static void unregister() {
		synchronized (waiting) {
			instance = null;
		}
	}
}
