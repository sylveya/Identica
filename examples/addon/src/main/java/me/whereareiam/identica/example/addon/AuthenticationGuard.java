package me.whereareiam.identica.example.addon;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import me.whereareiam.identica.pipeline.PendingScenarioService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Holds back the chat and the commands of a player who still has to authenticate, register or migrate.
 *
 * <p>Velocity cannot deny a signed chat message or command of a 1.19.1+ client without disconnecting it. Players
 * of an offline-mode network do not sign them; an addon for signed chat filters packets instead.</p>
 */
public final class AuthenticationGuard {
	static final Component CHAT_HELD_BACK = Component.text("Log in before you chat.", NamedTextColor.RED);
	static final Component COMMAND_HELD_BACK = Component.text("Log in before you use this command.", NamedTextColor.RED);

	private final PendingScenarioService pendingScenarios;
	private final AllowedCommands allowedCommands;

	public AuthenticationGuard(PendingScenarioService pendingScenarios, AllowedCommands allowedCommands) {
		this.pendingScenarios = pendingScenarios;
		this.allowedCommands = allowedCommands;
	}

	@Subscribe
	public void onChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		if (!pendingScenarios.isPending(player.getUniqueId())) return;

		event.setResult(PlayerChatEvent.ChatResult.denied());
		player.sendMessage(CHAT_HELD_BACK);
	}

	@Subscribe
	public void onCommand(CommandExecuteEvent event) {
		CommandSource source = event.getCommandSource();
		if (!(source instanceof Player player)) return;
		if (!pendingScenarios.isPending(player.getUniqueId())) return;
		if (allowedCommands.allows(event.getCommand())) return;

		event.setResult(CommandExecuteEvent.CommandResult.denied());
		player.sendMessage(COMMAND_HELD_BACK);
	}
}
