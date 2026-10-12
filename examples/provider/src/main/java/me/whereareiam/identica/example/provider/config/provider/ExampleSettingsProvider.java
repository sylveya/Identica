package me.whereareiam.identica.example.provider.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.config.ConfigProvider;
import me.whereareiam.identica.example.provider.config.ExampleSettings;
import me.whereareiam.identica.example.provider.config.defaults.ExampleSettingsDefaults;

import java.nio.file.Path;

/**
 * Loads {@code settings.yml} from the provider's working directory, {@code providers/Example}, writes its defaults
 * there on first start, and reloads it with {@code /identica admin reload}.
 */
@Singleton
public class ExampleSettingsProvider extends ConfigProvider<ExampleSettings> {
	@Inject
	public ExampleSettingsProvider(@Named("workingPath") Path workingPath, Registry<Reloadable> reloadables) {
		super(workingPath, "settings", ExampleSettings.class, reloadables);
	}

	@Override
	protected Configura configura() {
		return Config.configured().withDefaults(ExampleSettingsDefaults.class);
	}
}
