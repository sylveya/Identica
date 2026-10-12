package me.whereareiam.identica.testing.environment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Providers the test network installs, with the priority and entrypoint host it gives them. Premium outranks
 * Credential, as the installation guide recommends for mixed setups. Example is the provider the developer
 * documentation builds; it ranks last.
 */
@Getter
@RequiredArgsConstructor
public enum Provider {
	PREMIUM("premium", 200, "premium.identica.test"),
	CREDENTIAL("credential", 100, "credential.identica.test"),
	EXAMPLE("example", 50, "example.identica.test");

	private final String id;
	private final int priority;
	private final String entrypoint;
}
