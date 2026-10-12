package me.whereareiam.identica.example.provider.resolver;

import me.whereareiam.identica.example.provider.ExampleProvider;
import me.whereareiam.identica.provider.subject.SubjectResolution;
import me.whereareiam.identica.provider.subject.SubjectResolveContext;
import me.whereareiam.identica.provider.subject.SubjectResolver;
import me.whereareiam.identica.util.UniqueIdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Names a player's account at this provider. Identica links accounts to providers by this subject, so a
 * returning player is recognised; this provider derives it from the username, as an offline-mode server does.
 */
public final class ExampleSubjectResolver implements SubjectResolver {
	@Override
	public boolean supports(@NotNull SubjectResolveContext context) {
		String username = context.getUsername();
		return username != null && !username.isBlank();
	}

	@Override
	public @Nullable SubjectResolution resolve(@NotNull SubjectResolveContext context) {
		UUID subject = UniqueIdGenerator.offlinePlayerUniqueId(context.getUsername());
		if (subject == null) return null;

		return SubjectResolution.builder()
				.providerId(ExampleProvider.ID)
				.providerSubject(subject.toString())
				.build();
	}
}
