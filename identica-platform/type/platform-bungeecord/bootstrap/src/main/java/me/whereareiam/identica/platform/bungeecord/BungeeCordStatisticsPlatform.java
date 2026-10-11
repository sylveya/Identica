package me.whereareiam.identica.platform.bungeecord;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.identica.integration.statistics.StatisticsPlatform;
import me.whereareiam.statistica.common.StatisticaBuilder;
import me.whereareiam.statistica.platform.bungeecord.BungeeCordStatistica;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.ProtocolConstants;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Singleton
public final class BungeeCordStatisticsPlatform implements StatisticsPlatform {
	private static final int BSTATS_ID = 31292;

	/** BungeeCord names protocol numbers only as constants, such as {@code MINECRAFT_1_21_4}. */
	private static final Map<Integer, String> VERSIONS = versions();

	private final @NotNull BungeeCordIdentica plugin;

	@Inject
	public BungeeCordStatisticsPlatform(@NotNull BungeeCordIdentica plugin) {
		this.plugin = plugin;
	}

	@Override
	public @NotNull StatisticaBuilder<?> builder() {
		return BungeeCordStatistica.builder(plugin).bstats(BSTATS_ID);
	}

	@Override
	public @NotNull Map<String, Integer> clientVersions() {
		Map<String, Integer> versions = new TreeMap<>();
		for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
			int protocol = player.getPendingConnection().getVersion();
			versions.merge(VERSIONS.getOrDefault(protocol, "protocol " + protocol), 1, Integer::sum);
		}

		return versions;
	}

	private static @NotNull Map<Integer, String> versions() {
		Map<Integer, String> versions = new HashMap<>();
		for (Field field : ProtocolConstants.class.getFields()) {
			if (!Modifier.isStatic(field.getModifiers()) || field.getType() != int.class || !field.getName().startsWith("MINECRAFT_"))
				continue;

			try {
				String version = field.getName().substring("MINECRAFT_".length()).replace('_', '.');
				// Several releases can share a protocol; the constant of the newest one wins.
				versions.merge(field.getInt(null), version, (current, next) -> newer(current, next));
			} catch (IllegalAccessException ignored) {
			}
		}

		return versions;
	}

	private static @NotNull String newer(@NotNull String first, @NotNull String second) {
		String[] a = first.split("\\.");
		String[] b = second.split("\\.");
		for (int i = 0; i < Math.max(a.length, b.length); i++) {
			int x = i < a.length ? Integer.parseInt(a[i]) : 0;
			int y = i < b.length ? Integer.parseInt(b[i]) : 0;
			if (x != y) return x > y ? first : second;
		}

		return first;
	}
}
