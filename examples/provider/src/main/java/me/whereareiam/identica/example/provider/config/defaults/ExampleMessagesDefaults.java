package me.whereareiam.identica.example.provider.config.defaults;

import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.identica.example.provider.config.ExampleMessages;

public class ExampleMessagesDefaults implements DefaultsProvider<ExampleMessages> {
	@Override
	public ExampleMessages supply(ExampleMessages messages) {
		messages.setPrompt("{prefix}<white>Type <yellow>/passphrase</yellow> <gray>[Passphrase]</gray> to continue.</white>");
		messages.setInvalid("{prefix}<white>That passphrase is <red>wrong</red>.</white>");
		messages.setNoPending("{prefix}<white>Nothing is waiting for a passphrase.</white>");
		return messages;
	}
}
