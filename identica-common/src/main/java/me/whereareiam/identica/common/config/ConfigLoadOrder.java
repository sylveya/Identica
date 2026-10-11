package me.whereareiam.identica.common.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.identica.config.ConfigExtensionProvider;
import me.whereareiam.identica.config.ConfigProvider;

import java.util.Comparator;

/**
 * The order in which configuration documents are loaded and reloaded: every provider that owns its document
 * first, then the extensions of documents owned by others, then the reloadable components that are not documents,
 * which build on what the documents hold. Within each group the order is by class name, so it is the same on every
 * start.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigLoadOrder {
	/**
	 * Orders provider or reloadable classes for loading.
	 */
	public static final Comparator<Class<?>> BY_TYPE = Comparator
			.<Class<?>>comparingInt(ConfigLoadOrder::group)
			.thenComparing(Class::getSimpleName)
			.thenComparing(Class::getName);

	private static int group(Class<?> type) {
		if (ConfigExtensionProvider.class.isAssignableFrom(type)) return 1;
		if (ConfigProvider.class.isAssignableFrom(type)) return 0;
		return 2;
	}
}
