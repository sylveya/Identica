package me.whereareiam.identica.testing.entrypoint;

import me.whereareiam.anvil.api.model.player.SessionIdentity;
import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.environment.yggdrasil.YggdrasilMock;
import me.whereareiam.identica.testing.environment.Identica;
import me.whereareiam.identica.testing.environment.Provider;
import me.whereareiam.identica.testing.journey.Client;
import me.whereareiam.identica.testing.journey.Journey;
import me.whereareiam.identica.type.pipeline.journey.JourneyMode;
import org.junit.jupiter.api.Test;

import static me.whereareiam.identica.testing.environment.IdenticaNetwork.AUTH;
import static me.whereareiam.identica.testing.environment.IdenticaNetwork.LOBBY;

/**
 * New players join a seamless network whose providers answer on their own hostnames, {@code premium.identica.test}
 * and {@code credential.identica.test}. The premium hostname demands a premium login from everyone who joins
 * through it.
 */
class ProviderEntrypointJoinTest {
	private static final String PASSWORD = "Secret-123";

	@Test
	@Identica(mode = JourneyMode.SEAMLESS, entrypoints = true)
	void registersAPremiumOwnerWithPremiumThroughItsHostname(ScenarioContext anvil, YggdrasilMock yggdrasil) {
		SessionIdentity account = yggdrasil.register("Steve");

		Journey.of(anvil, Client.premium(account).through(Provider.PREMIUM.getEntrypoint())).join()
				.on(LOBBY)
				.sees(m -> m.premium().getCompletion().getRegistration().getBody());
	}

	@Test
	@Identica(mode = JourneyMode.SEAMLESS, entrypoints = true)
	void registersAnOfflinePlayerWithCredentialThroughItsHostname(ScenarioContext anvil) {
		Journey.of(anvil, Client.offline("Alice").through(Provider.CREDENTIAL.getEntrypoint())).join()
				.on(AUTH)
				.sees(m -> m.credential().getScenario().getRegistration().getPrompt())
				.register(PASSWORD)
				.on(LOBBY)
				.sees(m -> m.credential().getCompletion().getRegistration().getBody());
	}

	@Test
	@Identica(mode = JourneyMode.SEAMLESS, entrypoints = true)
	void demandsAPremiumLoginFromAnOfflinePlayerThroughThePremiumHostname(ScenarioContext anvil) {
		Journey.of(anvil, Client.offline("Alice").through(Provider.PREMIUM.getEntrypoint())).attempt()
				.authenticationRequired();
	}
}
