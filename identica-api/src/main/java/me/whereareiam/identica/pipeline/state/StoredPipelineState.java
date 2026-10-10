package me.whereareiam.identica.pipeline.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.identica.model.pipeline.PipelineCursor;
import me.whereareiam.identica.type.pipeline.PipelineType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The saved form of a {@link PipelineState}: its persisted values, each encoded under its key id. A pipeline state
 * store saves and reads this form; pipelines work on {@link PipelineState} through {@link PipelineState#store()} and
 * {@link PipelineState#restore(StoredPipelineState)}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoredPipelineState {
	private @Nullable PipelineType pipelineType;
	private @Nullable PipelineCursor cursor;
	private long updatedAt;
	/**
	 * Encoded persisted values by key id.
	 */
	private @NotNull Map<String, StoredValue> values = new LinkedHashMap<>();

	/**
	 * One encoded value.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StoredValue {
		private @NotNull String payload;
		/**
		 * When the value expires, in epoch milliseconds, or {@code 0} when it lives as long as the state.
		 */
		private long expiresAt;
	}
}
