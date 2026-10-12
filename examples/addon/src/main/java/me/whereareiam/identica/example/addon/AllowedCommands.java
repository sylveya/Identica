package me.whereareiam.identica.example.addon;

import me.whereareiam.identica.IdenticaAPI;
import me.whereareiam.identica.event.EventListener;
import me.whereareiam.identica.event.base.IdenticEvent;
import me.whereareiam.identica.event.lifecycle.IdenticaReloadedEvent;
import me.whereareiam.identica.model.config.MessageDocument;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Set;

/**
 * The commands a player may run before they have authenticated, read from Identica instead of listed by hand:
 * an administrator can rename {@code /login}, and a provider can add commands of its own.
 */
public final class AllowedCommands implements EventListener {
	private final IdenticaAPI identica;
	private final Logger logger;
	private volatile Set<String> commands = Set.of();

	public AllowedCommands(IdenticaAPI identica, Logger logger) {
		this.identica = identica;
		this.logger = logger;
	}

	/**
	 * Reads the commands again after {@code /identica admin reload}, which may have renamed them.
	 */
	@IdenticEvent
	public void onReloaded(IdenticaReloadedEvent event) {
		rebuild();
	}

	public void rebuild() {
		commands = Set.copyOf(identica.getCommandService().getCommandsAvailableWhilePending());

		int messageDocuments = identica.getConfigCatalog().documents(MessageDocument.class).size();
		logger.info("Example addon allows {} while authenticating and sees {} message documents", commands.stream().sorted().toList(), messageDocuments);
	}

	/**
	 * Returns whether a typed command, without its slash, is an allowed command or starts with one. A command can
	 * have several words, such as {@code 2fa confirm} or {@code identica enroll}.
	 *
	 * @param commandLine typed command
	 * @return {@code true} when the command is allowed
	 */
	public boolean allows(String commandLine) {
		String typed = commandLine.trim().toLowerCase(Locale.ROOT);
		for (String command : commands) {
			if (typed.equals(command) || typed.startsWith(command + " ")) return true;
		}

		return false;
	}
}
