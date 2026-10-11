package me.whereareiam.identica.integration.statistics.metric;

import me.whereareiam.identica.event.EventManager;
import me.whereareiam.identica.event.scenario.authentication.AuthenticationResolvedEvent;
import me.whereareiam.identica.integration.statistics.OfficialProviders;
import me.whereareiam.identica.model.auth.AuthContext;
import me.whereareiam.identica.model.provider.ProviderContext;
import me.whereareiam.identica.provider.ProviderManager;
import me.whereareiam.identica.type.ScenarioResolution;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActivityCountersTest {
	@Test
	void countsCompletedLoginsByProvider() {
		ProviderManager providerManager = mock(ProviderManager.class);
		when(providerManager.getProviders()).thenReturn(List.of());
		ActivityCounters counters = new ActivityCounters(mock(EventManager.class), new OfficialProviders(providerManager));

		counters.onAuthenticationResolved(resolved("premium", ScenarioResolution.COMPLETED));
		counters.onAuthenticationResolved(resolved("discord", ScenarioResolution.COMPLETED));
		counters.onAuthenticationResolved(resolved("credential", ScenarioResolution.FAILED));

		assertEquals(Map.of("premium", 1L, "custom", 1L), counters.getLoginsByProvider().totals());
		assertEquals(Map.of("completed", 2L, "failed", 1L), counters.getAuthentications().totals());
	}

	private static AuthenticationResolvedEvent resolved(String providerId, ScenarioResolution reason) {
		ProviderContext provider = mock(ProviderContext.class);
		when(provider.getProviderId()).thenReturn(providerId);
		AuthContext context = mock(AuthContext.class);
		when(context.getProvider()).thenReturn(provider);
		AuthenticationResolvedEvent event = mock(AuthenticationResolvedEvent.class);
		when(event.getContext()).thenReturn(context);
		when(event.getReason()).thenReturn(reason);

		return event;
	}
}
