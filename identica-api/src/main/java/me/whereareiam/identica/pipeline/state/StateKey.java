package me.whereareiam.identica.pipeline.state;

import me.whereareiam.identica.replication.codec.SnapshotCodec;
import me.whereareiam.identica.type.pipeline.StateScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Identifies one value in a {@link PipelineState}: its stable id, its type and whether it is ever saved.
 *
 * <p>The owner of a value declares its key once, next to the type it stores, and every reader and writer uses that
 * key. A persisted key also carries the codec its value is saved with.</p>
 *
 * <pre>{@code
 * public static final StateKey<CredentialRegisterStateItem> KEY =
 *         StateKey.persisted("credential:pending-password", CredentialRegisterStateItem.class);
 *
 * state.put(CredentialRegisterStateItem.KEY, pending);
 * Optional<CredentialRegisterStateItem> stored = state.get(CredentialRegisterStateItem.KEY);
 * }</pre>
 *
 * @param <T> value type
 */
public final class StateKey<T> {
	private final @NotNull String id;
	private final @NotNull Class<T> type;
	private final @NotNull StateScope scope;
	private final @Nullable SnapshotCodec<T> codec;

	private StateKey(
			@NotNull String id,
			@NotNull Class<T> type,
			@NotNull StateScope scope,
			@Nullable SnapshotCodec<T> codec
	) {
		if (id.isBlank()) throw new IllegalArgumentException("State key id must not be blank");

		this.id = id;
		this.type = type;
		this.scope = scope;
		this.codec = codec;
	}

	/**
	 * Creates a key for a value that only lives during one run and is never saved.
	 *
	 * @param id stable id, unique within a pipeline state
	 * @param type value type
	 * @param <T> value type
	 * @return run key
	 */
	public static <T> @NotNull StateKey<T> run(@NotNull String id, @NotNull Class<T> type) {
		return new StateKey<>(id, type, StateScope.RUN, null);
	}

	/**
	 * Creates a key for a value that is saved with a pending pipeline as JSON.
	 *
	 * @param id stable id, unique within a pipeline state; it is the name the value is saved under
	 * @param type value type, which must be readable back from JSON
	 * @param <T> value type
	 * @return persisted key
	 */
	public static <T> @NotNull StateKey<T> persisted(@NotNull String id, @NotNull Class<T> type) {
		return new StateKey<>(id, type, StateScope.PERSISTED, null);
	}

	/**
	 * Creates a key for a value that is saved with a pending pipeline through the given codec.
	 *
	 * @param id stable id, unique within a pipeline state; it is the name the value is saved under
	 * @param type value type
	 * @param codec codec the value is saved and read with
	 * @param <T> value type
	 * @return persisted key
	 */
	public static <T> @NotNull StateKey<T> persisted(
			@NotNull String id,
			@NotNull Class<T> type,
			@NotNull SnapshotCodec<T> codec
	) {
		return new StateKey<>(id, type, StateScope.PERSISTED, codec);
	}

	/**
	 * Returns the stable id of the key.
	 *
	 * @return id
	 */
	public @NotNull String id() {
		return id;
	}

	/**
	 * Returns the type of the value the key identifies.
	 *
	 * @return value type
	 */
	public @NotNull Class<T> type() {
		return type;
	}

	/**
	 * Returns whether the value outlives the run that holds it.
	 *
	 * @return scope
	 */
	public @NotNull StateScope scope() {
		return scope;
	}

	/**
	 * Returns the codec a persisted value is saved and read with. A key created without its own codec uses a JSON
	 * codec derived from the shared Configura instance at the time of the call, so it serializes with the modules
	 * Identica registered there.
	 *
	 * @return codec, or {@code null} for a run key
	 */
	public @Nullable SnapshotCodec<T> codec() {
		if (codec != null) return codec;

		return scope == StateScope.PERSISTED ? SnapshotCodec.json(type) : null;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof StateKey<?> key && id.equals(key.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return id;
	}
}
