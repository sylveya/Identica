package me.whereareiam.identica.example.provider.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

/**
 * {@code providers/Example/settings.yml}.
 */
@Getter
@Setter
@ToString(exclude = "passphrase")
public class ExampleSettings {
	/**
	 * The passphrase every player types to register and to log in.
	 */
	private @NotNull String passphrase;
}
