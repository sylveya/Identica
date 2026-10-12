package me.whereareiam.identica.example.provider;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.whereareiam.identica.example.provider.config.ExampleMessages;
import me.whereareiam.identica.example.provider.config.ExampleSettings;
import me.whereareiam.identica.example.provider.config.provider.ExampleMessagesProvider;
import me.whereareiam.identica.example.provider.config.provider.ExampleSettingsProvider;
import me.whereareiam.identica.example.provider.resolver.ExampleSubjectResolver;
import me.whereareiam.identica.provider.subject.SubjectResolver;

/**
 * Binds the provider's documents and tells Identica how to name a player's account at this provider.
 */
public class ExampleModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ExampleSettingsProvider.class).asEagerSingleton();
		bind(ExampleSettings.class).toProvider(ExampleSettingsProvider.class);
		bind(ExampleMessagesProvider.class).asEagerSingleton();
		bind(ExampleMessages.class).toProvider(ExampleMessagesProvider.class);

		Multibinder.newSetBinder(binder(), SubjectResolver.class)
				.addBinding()
				.to(ExampleSubjectResolver.class);
	}
}
