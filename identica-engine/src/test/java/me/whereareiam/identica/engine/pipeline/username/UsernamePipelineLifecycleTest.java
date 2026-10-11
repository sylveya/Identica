package me.whereareiam.identica.engine.pipeline.username;

import me.whereareiam.identica.pipeline.extension.PipelineExtensionRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("Username Pipeline Lifecycle")
class UsernamePipelineLifecycleTest {
	private final PipelineExtensionRegistry pipelines = mock(PipelineExtensionRegistry.class);
	private final UsernamePipelineExtension extension = mock(UsernamePipelineExtension.class);
	private final UsernamePipelineLifecycle lifecycle = new UsernamePipelineLifecycle(pipelines, extension);

	@Test
	@DisplayName("Registers the username phases and removes them on shutdown")
	void registersAndRemovesTheExtension() {
		when(extension.id()).thenReturn("authoritative-username:phases");

		lifecycle.initialize();
		verify(pipelines).register(extension);

		lifecycle.shutdown();
		verify(pipelines).unregister("authoritative-username:phases");
	}

	@Test
	@DisplayName("Shutdown before initialization leaves the pipelines untouched")
	void shutdownWithoutInitializeDoesNothing() {
		lifecycle.shutdown();

		verifyNoInteractions(pipelines);
	}
}
