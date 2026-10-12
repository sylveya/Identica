package me.whereareiam.identica.example.provider.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.identica.model.config.MessageDocument;
import org.jetbrains.annotations.NotNull;

/**
 * {@code providers/Example/messages.yml}. As a {@link MessageDocument}, addons find it next to Identica's own
 * messages.
 */
@Getter
@Setter
@ToString
public class ExampleMessages implements MessageDocument {
	/**
	 * Shown while Identica waits for the passphrase.
	 */
	private @NotNull String prompt;

	/**
	 * Shown when the typed passphrase is wrong; Identica keeps waiting.
	 */
	private @NotNull String invalid;

	/**
	 * Answer to {@code /passphrase} when nothing waits for one.
	 */
	private @NotNull String noPending;
}
