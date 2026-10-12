package me.whereareiam.identica.common;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("Default Identica API")
class DefaultIdenticaAPITest {
	private final BoundService bound = new BoundService() {};
	private final DefaultIdenticaAPI identica = Guice.createInjector(new AbstractModule() {
		@Override
		protected void configure() {
			bind(BoundService.class).toInstance(bound);
		}
	}).getInstance(DefaultIdenticaAPI.class);

	@DisplayName("Finds a bound service by its type")
	@Test
	void findsBoundService() {
		assertSame(bound, identica.findService(BoundService.class).orElseThrow());
	}

	@DisplayName("Does not construct a class nobody bound")
	@Test
	void unboundClassIsNotConstructed() {
		assertEquals(Optional.empty(), identica.findService(UnboundComponent.class));
	}

	private interface BoundService {
	}

	public static final class UnboundComponent {
	}
}
