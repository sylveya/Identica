package me.whereareiam.identica.example.provider.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.config.ConfigProvider;
import me.whereareiam.identica.example.provider.config.ExampleMessages;
import me.whereareiam.identica.example.provider.config.defaults.ExampleMessagesDefaults;

import java.nio.file.Path;

/**
 * Loads {@code messages.yml} from the provider's working directory, {@code providers/Example}, writes its defaults
 * there on first start, and reloads it with {@code /identica admin reload}.
 */
@Singleton
public class ExampleMessagesProvider extends ConfigProvider<ExampleMessages> {
	@Inject
	public ExampleMessagesProvider(@Named("workingPath") Path workingPath, Registry<Reloadable> reloadables) {
		super(workingPath, "messages", ExampleMessages.class, reloadables);
	}

	@Override
	protected Configura configura() {
		return Config.configured().withDefaults(ExampleMessagesDefaults.class);
	}
}
