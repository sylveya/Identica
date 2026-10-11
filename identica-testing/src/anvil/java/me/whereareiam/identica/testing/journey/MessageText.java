package me.whereareiam.identica.testing.journey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A configured message as the player reads it: without formatting tags, blank lines and lines that only hold a
 * placeholder. Placeholders match any text, so a message matches whoever it addresses.
 */
final class MessageText {
	private static final Pattern TAG = Pattern.compile("</?[a-zA-Z_#!?][^<>]*>");
	private static final Pattern PLACEHOLDER = Pattern.compile("\\{[a-zA-Z_]+}");

	private final List<String> lines;
	/** Every line with text, including lines that only hold a placeholder. */
	private final List<String> template;

	/**
	 * @param configured a message as configured: one line or a list of lines
	 */
	MessageText(Object configured) {
		List<String> plain = new ArrayList<>();
		List<String> all = new ArrayList<>();
		for (Object line : configured instanceof List<?> list ? list : List.of(configured)) {
			String text = TAG.matcher(String.valueOf(line)).replaceAll("").strip();
			if (!text.isBlank()) all.add(text);
			if (!PLACEHOLDER.matcher(text).replaceAll("").isBlank()) plain.add(text);
		}
		if (plain.isEmpty()) throw new IllegalArgumentException("Message has no text: " + configured);

		lines = List.copyOf(plain);
		template = List.copyOf(all);
	}

	/**
	 * Returns whether a received text contains every line of this message.
	 */
	boolean in(String received) {
		return lines.stream().allMatch(line -> pattern(line).matcher(received).find());
	}

	/**
	 * Returns what a placeholder holds in a received text that contains this message, as the player reads it.
	 * The placeholder may stand on a line of its own and span several lines, as a list the message hands out does.
	 *
	 * @param received text containing this message
	 * @param placeholder name of the placeholder, without braces
	 */
	String value(String received, String placeholder) {
		String target = "{" + placeholder + "}";
		if (template.stream().noneMatch(line -> line.contains(target)))
			throw new IllegalArgumentException("Message " + template + " has no placeholder " + target);

		StringBuilder expression = new StringBuilder();
		boolean captured = false;
		for (String line : template) {
			if (!expression.isEmpty()) expression.append("\\s*");
			var placeholders = PLACEHOLDER.matcher(line);
			int start = 0;
			while (placeholders.find()) {
				expression.append(Pattern.quote(line.substring(start, placeholders.start())));
				boolean value = !captured && placeholders.group().equals(target);
				expression.append(value ? "(?<value>[\\s\\S]+?)" : "[\\s\\S]*?");
				captured |= value;
				start = placeholders.end();
			}
			expression.append(Pattern.quote(line.substring(start)));
		}

		var matcher = Pattern.compile(expression.toString()).matcher(received);
		if (!matcher.find())
			throw new IllegalStateException("Message " + template + " is not in: " + received);

		return matcher.group("value").strip();
	}

	/**
	 * Returns the longest text between placeholders, to recognize the message in single-line output such as a
	 * console.
	 */
	String signature() {
		return lines.stream()
				.flatMap(line -> Arrays.stream(PLACEHOLDER.split(line)))
				.map(String::strip)
				.max(Comparator.comparingInt(String::length))
				.orElseThrow(() -> new IllegalStateException("Message has no text: " + lines));
	}

	@Override
	public String toString() {
		return lines.toString();
	}

	private static Pattern pattern(String line) {
		StringBuilder expression = new StringBuilder();
		for (String literal : PLACEHOLDER.split(line, -1)) {
			if (!expression.isEmpty()) expression.append(".*");
			expression.append(Pattern.quote(literal));
		}

		return Pattern.compile(expression.toString());
	}
}
