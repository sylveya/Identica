package me.whereareiam.identica.model.pipeline.journey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.identica.pipeline.state.StateKey;
import me.whereareiam.identica.type.pipeline.journey.JourneyMode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class JourneyOverrideItem {
	/**
	 * Key of the requested journey position and provider exclusions in a pipeline state. Saved with a pending pipeline.
	 */
	public static final StateKey<JourneyOverrideItem> KEY =
			StateKey.persisted("journey:override", JourneyOverrideItem.class);

	private @Nullable JourneyMode journeyMode;
	private @Nullable String stageId;
	private int stepIndex;
	private boolean clearProvider;
	private @Nullable String providerId;
	private @Nullable List<String> excludedProviders;
}
