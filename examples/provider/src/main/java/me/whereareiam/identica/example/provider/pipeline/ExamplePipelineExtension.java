package me.whereareiam.identica.example.provider.pipeline;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.identica.example.provider.ExampleProvider;
import me.whereareiam.identica.example.provider.pipeline.step.ExamplePassphraseStep;
import me.whereareiam.identica.pipeline.extension.PipelineExtension;
import me.whereareiam.identica.pipeline.extension.PipelineExtensionBuilder;
import me.whereareiam.identica.type.pipeline.journey.JourneyMode;
import me.whereareiam.identica.type.pipeline.journey.StageType;
import org.jetbrains.annotations.NotNull;

/**
 * Puts the passphrase step into this provider's stage of registration and authentication, in both journey modes.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ExamplePipelineExtension implements PipelineExtension {
	private final ExamplePassphraseStep passphraseStep;

	@Override
	public @NotNull String id() {
		return ExampleProvider.ID + ":passphrase";
	}

	@Override
	public void apply(@NotNull PipelineExtensionBuilder builder) {
		for (JourneyMode mode : JourneyMode.values())
			builder.registerStep(ExampleProvider.ID, StageType.PROVIDER, mode, passphraseStep);
	}
}
