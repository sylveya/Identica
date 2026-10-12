package me.whereareiam.identica.adapter.command.executor.admin;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.Serializer;
import me.whereareiam.identica.annotation.Command;
import me.whereareiam.identica.annotation.Definition;
import me.whereareiam.identica.event.EventManager;
import me.whereareiam.identica.event.lifecycle.IdenticaReloadedEvent;
import me.whereareiam.identica.logging.Logger;
import me.whereareiam.identica.model.config.Messages;
import me.whereareiam.keystone.Actor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reloads every component registered in the running proxy, in the registry's order. A component that fails to
 * reload keeps what it had and the others still reload; the answer is a success only when none failed.
 * Afterwards it calls {@link IdenticaReloadedEvent}, whatever failed, so addons rebuild what they derived.
 */
public class ReloadCommand {
	private final Registry<Reloadable> reloadables;
	private final Provider<Messages> messagesProvider;
	private final EventManager eventManager;

	@Inject
	public ReloadCommand(Registry<Reloadable> reloadables, Provider<Messages> messagesProvider, EventManager eventManager) {
		this.reloadables = reloadables;
		this.messagesProvider = messagesProvider;
		this.eventManager = eventManager;
	}

	@Definition("admin-reload")
	@Command("identica admin reload")
	public void command(@NotNull Actor sender) {
		List<String> failures = new ArrayList<>();
		for (Reloadable reloadable : reloadables.values()) {
			try {
				reloadable.reload();
			} catch (Exception failure) {
				String reason = failure.getMessage() != null ? failure.getMessage() : failure.getClass().getSimpleName();
				Logger.warn("Could not reload %s: %s", reloadable.getClass().getSimpleName(), reason);
				failures.add(reason);
			}
		}

		eventManager.call(new IdenticaReloadedEvent());

		Messages.Commands.Reload reload = messagesProvider.get().getCommands().getReload();
		if (failures.isEmpty()) {
			sender.sendMessage(Serializer.serialize(sender, reload.getSuccess()));
			return;
		}

		sender.sendMessage(Serializer.serialize(sender, reload.getError(), Map.of("error", String.join("; ", failures))));
	}
}
