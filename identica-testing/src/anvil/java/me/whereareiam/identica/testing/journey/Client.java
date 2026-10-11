package me.whereareiam.identica.testing.journey;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.anvil.api.model.player.PlayerConnection;
import me.whereareiam.anvil.api.model.player.PlayerLogin;
import me.whereareiam.anvil.api.model.player.SessionIdentity;
import me.whereareiam.anvil.api.type.AuthenticationMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The client a journey's player joins with: who logs in, which proxy it connects to, the hostname it announces
 * and the loopback address it connects from. Each change returns a new description.
 *
 * <pre>{@code
 * Journey alice = Journey.of(anvil, Client.offline("Alice").through(Provider.CREDENTIAL.getEntrypoint()).from("127.0.0.2"));
 * }</pre>
 */
@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Client {
	private final @NotNull String username;
	private final @Nullable SessionIdentity account;
	private final @Nullable String proxy;
	private final @Nullable String host;
	private final @Nullable String address;

	/**
	 * Describes an offline client, as a player without a premium account uses.
	 */
	public static Client offline(@NotNull String username) {
		return new Client(username, null, null, null, null);
	}

	/**
	 * Describes the client of a premium account registered with the network's stand-in for Mojang. It
	 * authenticates when the proxy asks for it, as a premium client does.
	 */
	public static Client premium(@NotNull SessionIdentity account) {
		return new Client(account.getUsername(), account, null, null, null);
	}

	/**
	 * Joins through one proxy of a cluster instead of the network's entrypoint.
	 */
	public Client at(@NotNull String proxy) {
		return new Client(username, account, proxy, host, address);
	}

	/**
	 * Announces a hostname in the handshake, as a player who typed that address does; the client still connects
	 * to the proxy's real address.
	 */
	public Client through(@NotNull String host) {
		return new Client(username, account, proxy, host, address);
	}

	/**
	 * Connects from another loopback address, such as {@code 127.0.0.2}, so the proxy sees the player arrive from
	 * there.
	 */
	public Client from(@NotNull String address) {
		return new Client(username, account, proxy, host, address);
	}

	/**
	 * Returns the player's name in the scenario: the username, followed by the proxy, the hostname and the address
	 * when the client names them, so several clients of one username can join.
	 */
	String playerName() {
		StringBuilder name = new StringBuilder(username);
		if (proxy != null) name.append('@').append(proxy);
		if (host != null) name.append('@').append(host);
		if (address != null) name.append('@').append(address);
		return name.toString();
	}

	PlayerLogin login() {
		return account != null ? PlayerLogin.session(AuthenticationMode.ON_REQUEST, account) : PlayerLogin.offline(username);
	}

	PlayerConnection connection() {
		return PlayerConnection.builder()
				.target(proxy)
				.virtualHost(host)
				.sourceAddress(address)
				.build();
	}
}
