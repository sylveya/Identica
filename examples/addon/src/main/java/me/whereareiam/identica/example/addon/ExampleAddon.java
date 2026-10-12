package me.whereareiam.identica.example.addon;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.whereareiam.identica.IdenticaAPI;
import org.slf4j.Logger;

/**
 * Holds back chat and commands until a player has authenticated, the way an addon such as a chat or command
 * blocker would. It declares Identica as a dependency, so Velocity loads Identica first.
 */
@Plugin(
		id = "identica-example-addon",
		name = "IdenticaExampleAddon",
		version = "1.0.0",
		description = "Example addon built on Identica's API",
		dependencies = @Dependency(id = "identica")
)
public final class ExampleAddon {
	private final ProxyServer proxy;
	private final Logger logger;

	/**
	 * Velocity creates the plugin before Identica has started, so the addon waits for Identica to be ready: started,
	 * with its providers loaded and their commands and documents in place.
	 */
	@Inject
	public ExampleAddon(ProxyServer proxy, Logger logger) {
		this.proxy = proxy;
		this.logger = logger;
		IdenticaAPI.whenReady(this::enable);
	}

	private void enable(IdenticaAPI identica) {
		AllowedCommands allowedCommands = new AllowedCommands(identica, logger);
		allowedCommands.rebuild();
		identica.getEventManager().register(allowedCommands);

		proxy.getEventManager().register(this, new AuthenticationGuard(identica.getPendingScenarioService(), allowedCommands));
		logger.info("Example addon enabled");
	}
}
