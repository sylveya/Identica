package me.whereareiam.identica.integration.statistics.metric;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import me.whereareiam.identica.event.EventListener;
import me.whereareiam.identica.event.EventManager;
import me.whereareiam.identica.event.base.IdenticEvent;
import me.whereareiam.identica.event.scenario.authentication.AuthenticationResolvedEvent;
import me.whereareiam.identica.event.scenario.migration.MigrationResolvedEvent;
import me.whereareiam.identica.event.scenario.registration.RegistrationResolvedEvent;
import me.whereareiam.identica.event.session.SessionOpenedEvent;
import me.whereareiam.identica.type.event.EventOrder;
import me.whereareiam.statistica.metric.Counter;
import me.whereareiam.statistica.metric.CounterMap;
import org.jetbrains.annotations.NotNull;

/**
 * Counts what players do on this proxy: logins, registrations and migrations by how they ended, and
 * sessions opened. Counting is a lock-free increment on the login path; nothing else happens there.
 * <p>
 * These events are not replicated, so each proxy of a cluster counts only its own players.
 */
@Getter
@Singleton
public final class ActivityCounters implements EventListener {
	private final @NotNull CounterMap authentications = new CounterMap();
	private final @NotNull CounterMap registrations = new CounterMap();
	private final @NotNull CounterMap migrations = new CounterMap();
	private final @NotNull Counter sessions = new Counter();

	@Inject
	public ActivityCounters(@NotNull EventManager eventManager) {
		eventManager.register(this);
	}

	@IdenticEvent(EventOrder.HIGHEST)
	public void onAuthenticationResolved(@NotNull AuthenticationResolvedEvent event) {
		authentications.increment(event.getReason());
	}

	@IdenticEvent(EventOrder.HIGHEST)
	public void onRegistrationResolved(@NotNull RegistrationResolvedEvent event) {
		registrations.increment(event.getReason());
	}

	@IdenticEvent(EventOrder.HIGHEST)
	public void onMigrationResolved(@NotNull MigrationResolvedEvent event) {
		migrations.increment(event.getReason());
	}

	@IdenticEvent(EventOrder.HIGHEST)
	public void onSessionOpened(@NotNull SessionOpenedEvent event) {
		sessions.increment();
	}
}
