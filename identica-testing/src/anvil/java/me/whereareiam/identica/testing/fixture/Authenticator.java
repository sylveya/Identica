package me.whereareiam.identica.testing.fixture;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * An authenticator app holding the secret a player was shown, computing time-based codes the way such apps do
 * (RFC 6238 with HMAC-SHA1). It is written independently of Identica, so a journey fails when Identica's codes
 * differ from what a real app would show.
 */
public final class Authenticator {
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

	private final byte[] key;
	private final int digits;
	private final Duration period;

	private Authenticator(byte[] key, int digits, Duration period) {
		this.key = key;
		this.digits = digits;
		this.period = period;
	}

	/**
	 * Adds a secret as an app does by default: six-digit codes that change every 30 seconds.
	 *
	 * @param secret Base32 secret as the player read it
	 */
	public static Authenticator of(String secret) {
		return new Authenticator(decode(secret), 6, Duration.ofSeconds(30));
	}

	/**
	 * Returns the code the app shows now.
	 */
	public String code() {
		return code(window(Instant.now()));
	}

	/**
	 * Returns a well-formed code the app does not show now and did not show in the windows around now.
	 */
	public String wrongCode() {
		long now = window(Instant.now());
		Set<String> nearby = new HashSet<>();
		for (long offset = -3; offset <= 3; offset++)
			nearby.add(code(now + offset));

		int modulus = (int) Math.pow(10, digits);
		int candidate = Integer.parseInt(code(now));
		String wrong;
		do {
			candidate = (candidate + 1) % modulus;
			wrong = format(candidate);
		} while (nearby.contains(wrong));

		return wrong;
	}

	private long window(Instant instant) {
		return instant.getEpochSecond() / period.toSeconds();
	}

	private String code(long window) {
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(key, "HmacSHA1"));
			byte[] hash = mac.doFinal(ByteBuffer.allocate(Long.BYTES).putLong(window).array());
			int offset = hash[hash.length - 1] & 0x0F;
			int binary = ((hash[offset] & 0x7F) << 24)
					| ((hash[offset + 1] & 0xFF) << 16)
					| ((hash[offset + 2] & 0xFF) << 8)
					| (hash[offset + 3] & 0xFF);
			return format(binary % (int) Math.pow(10, digits));
		} catch (GeneralSecurityException failure) {
			throw new IllegalStateException("HMAC-SHA1 is not available", failure);
		}
	}

	private String format(int code) {
		return String.format(Locale.ROOT, "%0" + digits + "d", code);
	}

	private static byte[] decode(String secret) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		int buffer = 0;
		int bits = 0;
		for (char character : secret.replace(" ", "").replace("=", "").toUpperCase(Locale.ROOT).toCharArray()) {
			int value = ALPHABET.indexOf(character);
			if (value < 0) throw new IllegalArgumentException("Not a Base32 secret: " + secret);

			buffer = (buffer << 5) | value;
			bits += 5;
			if (bits < 8) continue;

			bytes.write((buffer >> (bits - 8)) & 0xFF);
			bits -= 8;
		}

		return bytes.toByteArray();
	}
}
