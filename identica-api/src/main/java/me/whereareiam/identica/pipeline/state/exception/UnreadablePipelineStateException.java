package me.whereareiam.identica.pipeline.state.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when a saved pipeline state value cannot be read back, for example after its type changed. A pipeline that
 * meets it discards the whole pending state, because acting on part of it could rely on wrong assumptions.
 */
@Getter
public class UnreadablePipelineStateException extends RuntimeException {
	/**
	 * Id of the state key whose saved value could not be read.
	 */
	private final @NotNull String keyId;

	/**
	 * Creates the exception for a key whose saved value could not be read.
	 *
	 * @param keyId id of the state key
	 * @param cause failure of the codec that read the value
	 */
	public UnreadablePipelineStateException(@NotNull String keyId, @NotNull Throwable cause) {
		super("Saved pipeline state value '" + keyId + "' cannot be read", cause);
		this.keyId = keyId;
	}
}
