package me.whereareiam.identica.adapter.command.executor.admin;

import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.Serializer;
import me.whereareiam.identica.event.EventManager;
import me.whereareiam.identica.event.lifecycle.IdenticaReloadedEvent;
import me.whereareiam.identica.model.config.Messages;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.SerializerEngine;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Reload Command")
class ReloadCommandTest {
	private final List<SerializerContent> answers = new ArrayList<>();
	private final List<String> reloaded = new ArrayList<>();
	private final EventManager eventManager = mock(EventManager.class);

	@BeforeEach
	void recordAnswers() {
		SerializerEngine engine = mock(SerializerEngine.class);
		when(engine.serialize(any(SerializerContent.class))).thenAnswer(call -> {
			answers.add(call.getArgument(0, SerializerContent.class));
			return Component.empty();
		});
		Serializer.initialize(() -> engine);
	}

	@DisplayName("Reloads every registered component in the registry's order and answers with success")
	@Test
	void reloadsEveryComponentInOrder() {
		command(reloadable("settings"), reloadable("providers"), reloadable("serializer")).command(mock(Actor.class));

		assertEquals(List.of("settings", "providers", "serializer"), reloaded);
		assertEquals(1, answers.size());
		assertEquals("success", answers.getFirst().getMessage());
	}

	@DisplayName("A component that fails keeps the others reloading and the answer is the error with its reason")
	@Test
	void failureIsReportedAfterTheOthersReloaded() {
		Reloadable broken = () -> {
			throw new IllegalStateException("Failed to read config tree: settings.yml");
		};

		command(reloadable("messages"), broken, reloadable("serializer")).command(mock(Actor.class));

		assertEquals(List.of("messages", "serializer"), reloaded);
		assertEquals(1, answers.size());
		assertEquals("error", answers.getFirst().getMessage());
		assertEquals("Failed to read config tree: settings.yml", answers.getFirst().getPlaceholder("error"));
	}

	@DisplayName("Calls the reloaded event once, after every component, also when one failed")
	@Test
	void reloadedEventFollowsTheComponents() {
		doAnswer(call -> {
			assertInstanceOf(IdenticaReloadedEvent.class, call.getArgument(0));
			reloaded.add("event");
			return null;
		}).when(eventManager).call(any());
		Reloadable broken = () -> {
			throw new IllegalStateException("Failed to read config tree: messages.yml");
		};

		command(reloadable("settings"), broken, reloadable("serializer")).command(mock(Actor.class));

		assertEquals(List.of("settings", "serializer", "event"), reloaded);
	}

	private Reloadable reloadable(String name) {
		return () -> reloaded.add(name);
	}

	private ReloadCommand command(Reloadable... reloadables) {
		Set<Reloadable> ordered = new LinkedHashSet<>(List.of(reloadables));
		@SuppressWarnings("unchecked")
		Registry<Reloadable> registry = mock(Registry.class);
		when(registry.values()).thenReturn(ordered);

		return new ReloadCommand(registry, ReloadCommandTest::messages, eventManager);
	}

	private static Messages messages() {
		Messages.Commands.Reload reload = new Messages.Commands.Reload();
		reload.setSuccess("success");
		reload.setError("error");
		Messages.Commands commands = new Messages.Commands();
		commands.setReload(reload);
		Messages messages = new Messages();
		messages.setCommands(commands);
		return messages;
	}
}
