package me.whereareiam.identica.example.provider.pipeline;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.identica.pipeline.state.StateKey;

/**
 * The passphrase a player typed. The command hands it to the pipeline run it starts; it is never saved.
 */
@Getter
@RequiredArgsConstructor
public class ExamplePassphraseAttempt {
	public static final StateKey<ExamplePassphraseAttempt> KEY = StateKey.run("example:passphrase-attempt", ExamplePassphraseAttempt.class);

	private final String passphrase;
}
