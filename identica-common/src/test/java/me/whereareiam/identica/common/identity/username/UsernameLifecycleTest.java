package me.whereareiam.identica.common.identity.username;

import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.common.config.provider.UsernameConflictsProvider;
import me.whereareiam.identica.common.config.provider.UsernameMessagesProvider;
import me.whereareiam.identica.conflict.ConflictService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

@DisplayName("Username Lifecycle")
class UsernameLifecycleTest {
	@SuppressWarnings("unchecked")
	private final Registry<Reloadable> reloadables = mock(Registry.class);
	private final UsernameConflictsProvider conflictsProvider = mock(UsernameConflictsProvider.class);
	private final UsernameMessagesProvider messagesProvider = mock(UsernameMessagesProvider.class);
	private final ConflictService conflicts = mock(ConflictService.class);
	private final UsernameConflictType conflictType = mock(UsernameConflictType.class);
	private final UsernameLifecycle lifecycle =
			new UsernameLifecycle(reloadables, conflictsProvider, messagesProvider, conflicts, conflictType);

	@Test
	@DisplayName("Loads the conflict rules and registers the username conflict type")
	void initializeLoadsRulesAndRegistersConflictType() {
		lifecycle.initialize();

		InOrder order = inOrder(conflictsProvider, conflicts);
		order.verify(conflictsProvider).get();
		order.verify(conflicts).register(conflictType);
		verifyNoInteractions(reloadables);
	}

	@Test
	@DisplayName("Shutdown undoes every registration in reverse order")
	void shutdownUndoesRegistrations() {
		lifecycle.initialize();
		lifecycle.shutdown();

		InOrder order = inOrder(conflicts, reloadables);
		order.verify(conflicts).unregister(conflictType);
		order.verify(reloadables).unregister(messagesProvider);
		order.verify(reloadables).unregister(conflictsProvider);
	}
}
