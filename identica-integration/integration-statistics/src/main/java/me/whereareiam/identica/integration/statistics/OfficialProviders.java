package me.whereareiam.identica.integration.statistics;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.identica.model.provider.InternalProvider;
import me.whereareiam.identica.model.provider.ProviderDescriptor;
import me.whereareiam.identica.provider.ProviderManager;
import me.whereareiam.identica.type.provider.ProviderState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Tells Identica's own providers from third-party ones.
 * <p>
 * Statistics name only Identica's providers and count every other one as {@code custom}: a
 * third-party provider's id is a server's own choice. Errors are only reported from Identica's
 * providers, whose code Identica owns.
 */
@Singleton
public final class OfficialProviders {
	/** Sent in place of a third-party provider's id. */
	public static final String CUSTOM = "custom";

	private static final Set<String> IDS = Set.of("credential", "premium");
	private static final String PACKAGE = "me.whereareiam.identica.provider.";

	private final @NotNull ProviderManager providerManager;

	@Inject
	public OfficialProviders(@NotNull ProviderManager providerManager) {
		this.providerManager = providerManager;
	}

	/**
	 * Returns the id a provider is counted under.
	 *
	 * @param provider the provider
	 * @return its id when it is one of Identica's providers, otherwise {@link #CUSTOM}
	 */
	public @NotNull String statisticsId(@NotNull InternalProvider provider) {
		ProviderDescriptor descriptor = provider.getDescriptor();
		return isOfficial(descriptor) ? normalize(descriptor.getId()) : CUSTOM;
	}

	/**
	 * Returns the id a stored provider id is counted under.
	 *
	 * @param providerId id stored with an account
	 * @return the id when it names one of Identica's providers, otherwise {@link #CUSTOM}
	 */
	public @NotNull String statisticsId(@NotNull String providerId) {
		String id = normalize(providerId);
		return IDS.contains(id) ? id : CUSTOM;
	}

	/**
	 * Returns the class loaders of Identica's providers that are running.
	 *
	 * @return their class loaders
	 */
	public @NotNull List<ClassLoader> classLoaders() {
		List<ClassLoader> loaders = new ArrayList<>(IDS.size());
		for (InternalProvider provider : providerManager.getProviders()) {
			ClassLoader loader = provider.getClassLoader();
			if (loader != null && provider.getState() == ProviderState.ENABLED && isOfficial(provider.getDescriptor()))
				loaders.add(loader);
		}

		return loaders;
	}

	private static boolean isOfficial(@Nullable ProviderDescriptor descriptor) {
		return descriptor != null
				&& IDS.contains(normalize(descriptor.getId()))
				&& descriptor.getMain().startsWith(PACKAGE);
	}

	private static @NotNull String normalize(@NotNull String providerId) {
		return providerId.trim().toLowerCase(Locale.ROOT);
	}
}
