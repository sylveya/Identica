package me.whereareiam.identica.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.identica.Registry;
import me.whereareiam.identica.Reloadable;
import me.whereareiam.identica.config.ConfigProvider;
import me.whereareiam.identica.common.config.defaults.messages.UsernameMessagesDefaults;
import me.whereareiam.identica.model.config.UsernameMessages;

import java.nio.file.Path;

@Singleton
public class UsernameMessagesProvider extends ConfigProvider<UsernameMessages> {
	@Inject
	public UsernameMessagesProvider(
			@Named("usernamePath") Path featurePath,
			Registry<Reloadable> reloadables
	) {
		super(featurePath, "messages", UsernameMessages.class, reloadables);
	}

	@Override
	protected Configura configura() {
		return Config.configured().withDefaults(UsernameMessagesDefaults.class);
	}
}
