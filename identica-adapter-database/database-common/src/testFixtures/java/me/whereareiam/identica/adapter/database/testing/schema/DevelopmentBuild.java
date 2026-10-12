package me.whereareiam.identica.adapter.database.testing.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Creates the tables an owner's schema had in a development build of Identica 3.0.0, before its
 * tables were installed through migrations. The DDL of each owner is a test resource per database
 * type, {@code schema/development-build/<owner>/<type>.sql}, generated from that build's
 * hand-written statements. Each block after a {@code -- statement} line runs as one JDBC statement,
 * as that build ran it, so a driver that executes only the first of several statements behaves as
 * it did then.
 */
public final class DevelopmentBuild {
	private static final String SEPARATOR = "-- statement";

	private DevelopmentBuild() {
	}

	/**
	 * Creates an owner's tables in the given database.
	 *
	 * @param database the database, after {@link TestDatabase#reset()}
	 * @param owner    the owner's directory, such as {@code account}
	 */
	public static void create(TestDatabase database, String owner) {
		List<String> statements = statements(owner, database.type().name().toLowerCase(Locale.ROOT));
		database.connect(connection -> {
			try (Statement statement = connection.createStatement()) {
				for (String sql : statements)
					statement.execute(sql);
			}
			if (!connection.getAutoCommit()) connection.commit();
			return null;
		});
	}

	private static List<String> statements(String owner, String type) {
		String resource = "schema/development-build/" + owner + "/" + type + ".sql";
		String script;
		try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
			if (input == null) throw new IllegalArgumentException("Missing test resource " + resource);
			script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		List<String> statements = new ArrayList<>();
		StringBuilder current = null;
		for (String line : script.split("\n")) {
			if (line.strip().equals(SEPARATOR)) {
				if (current != null) statements.add(current.toString().strip());
				current = new StringBuilder();
			} else if (current != null) {
				current.append(line).append('\n');
			}
		}
		if (current != null) statements.add(current.toString().strip());

		return statements;
	}
}
