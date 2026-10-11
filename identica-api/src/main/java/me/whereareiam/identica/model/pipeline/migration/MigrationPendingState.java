package me.whereareiam.identica.model.pipeline.migration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.identica.pipeline.state.StateKey;
import me.whereareiam.identica.type.migration.MigrationInitiator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class MigrationPendingState {
	/**
	 * Key of the pending provider migration in a pipeline state. Saved with a pending pipeline.
	 */
	public static final StateKey<MigrationPendingState> KEY =
			StateKey.persisted("migration:pending", MigrationPendingState.class);

	private @NotNull String targetProviderId;
	private long requestedAt;
	private @Nullable MigrationInitiator initiator;
	private @Nullable UUID initiatorUniqueId;
}
