package me.whereareiam.identica.example.provider.config.defaults;

import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.identica.example.provider.config.ExampleSettings;

public class ExampleSettingsDefaults implements DefaultsProvider<ExampleSettings> {
	@Override
	public ExampleSettings supply(ExampleSettings settings) {
		settings.setPassphrase("OpenSesame");
		return settings;
	}
}
