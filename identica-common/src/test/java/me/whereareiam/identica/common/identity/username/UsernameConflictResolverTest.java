package me.whereareiam.identica.common.identity.username;

import me.whereareiam.identica.conflict.ConflictService;
import me.whereareiam.identica.identity.session.SessionService;
import me.whereareiam.identica.model.conflict.ConflictContext;
import me.whereareiam.identica.model.conflict.ConflictResolution;
import me.whereareiam.identica.conflict.UsernameConflictSchema;
import me.whereareiam.identica.model.config.UsernameMessages;
import me.whereareiam.identica.model.identity.username.UsernameConflictResult;
import me.whereareiam.identica.model.identity.username.UsernameConflictSubject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("Username Conflict Resolver")
class UsernameConflictResolverTest {
	@DisplayName("Applies incoming and existing effects before denying")
	@Test
	void appliesEffectsBeforeDenying() {
		ConflictService conflictService = mock(ConflictService.class);
		UsernameConflictType conflictType = mock(UsernameConflictType.class);
		SessionService sessionService = mock(SessionService.class);

		ConflictContext context = ConflictContext.builder()
				.key("username")
				.hook("prepare")
				.build();
		java.util.UUID uniqueId = java.util.UUID.randomUUID();
		me.whereareiam.identica.model.conflict.participant.ConflictParticipant existing =
				me.whereareiam.identica.model.conflict.participant.ConflictParticipant.builder()
						.role(UsernameConflictSchema.ROLE_EXISTING)
						.build();
		existing.putAttribute(UsernameConflictSchema.PARTICIPANT_ATTRIBUTE_ACCOUNT_UNIQUE_ID, uniqueId);
		context.putParticipant(existing);
		when(conflictType.createContext(any())).thenReturn(context);
		when(conflictService.resolve(context)).thenReturn(ConflictResolution.deny(null)
				.withEffect(UsernameConflictSchema.EFFECT_CLOSE_EXISTING, true)
				.withEffect(UsernameConflictSchema.EFFECT_EXISTING_EFFECTIVE_USERNAME, "Existing_1")
				.withEffect(UsernameConflictSchema.EFFECT_INCOMING_EFFECTIVE_USERNAME, "Incoming_1"));
		when(sessionService.close(uniqueId)).thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));
		when(sessionService.findAllByUniqueId(uniqueId)).thenReturn(java.util.concurrent.CompletableFuture.completedFuture(java.util.List.of()));

		UsernameConflictResolver resolver = new UsernameConflictResolver(
				conflictService,
				conflictType,
				sessionService,
				this::messages
		);

		UsernameConflictResult result = resolver.handle(
				UsernameConflictSubject.builder().build(),
				"Player"
		);

		assertTrue(result.isDenied());
		assertEquals("denied", result.getDenialMessage());
		assertEquals("Incoming_1", result.getEffectiveUsername());
		verify(sessionService).close(uniqueId);
		verify(sessionService).findAllByUniqueId(uniqueId);
	}

	private UsernameMessages messages() {
		UsernameMessages.Pipeline.Prepare prepare = new UsernameMessages.Pipeline.Prepare();
		prepare.setFailed(java.util.List.of("failed"));
		UsernameMessages.Pipeline.Identity identity = new UsernameMessages.Pipeline.Identity();
		identity.setSynchronizationFailed(java.util.List.of("sync"));
		UsernameMessages.Pipeline.Policy policy = new UsernameMessages.Pipeline.Policy();
		policy.setPersistenceFailed(java.util.List.of("persist"));
		policy.setConflictDenied(java.util.List.of("denied"));
		policy.setEntrypointRequired(java.util.List.of("entrypoint"));

		UsernameMessages.Pipeline pipeline = new UsernameMessages.Pipeline();
		pipeline.setPrepare(prepare);
		pipeline.setIdentity(identity);
		pipeline.setPolicy(policy);

		UsernameMessages messages = new UsernameMessages();
		messages.setPipeline(pipeline);
		return messages;
	}
}
