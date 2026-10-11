package me.whereareiam.identica.adapter.database.schema;

import me.whereareiam.identica.adapter.database.testing.schema.DatabaseSchemaContract;
import me.whereareiam.identica.adapter.database.testing.schema.SchemaSnapshot;
import me.whereareiam.identica.database.schema.DatabaseSchema;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import org.h2.jdbcx.JdbcDataSource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountSchemaTest extends DatabaseSchemaContract {
	private static final String ACCOUNT = "'00000000-0000-0000-0000-000000000001'";

	@Override
	protected DatabaseSchema schema() {
		return new AccountSchema();
	}

	@Override
	protected List<String> developmentBuild() {
		return List.of("account");
	}

	@Override
	protected List<String> tables() {
		return List.of(
				"identica_accounts",
				"identica_provider_links",
				"identica_provider_profiles",
				"identica_account_reservations",
				"identica_account_username_state",
				"identica_account_username_history"
		);
	}

	@Override
	protected List<String> rows() {
		return List.of(
				"INSERT INTO identica_accounts (unique_id, username, created_at, last_seen_at) VALUES (" + ACCOUNT + ", 'Alice', 1, 2)",
				"INSERT INTO identica_provider_links (provider_id, provider_subject, unique_id, is_primary, linked_at, last_seen_at) VALUES ('credential', 'alice', " + ACCOUNT + ", TRUE, 1, 2)",
				"INSERT INTO identica_provider_profiles (provider_id, provider_subject, provider_username) VALUES ('credential', 'alice', 'Alice')",
				"INSERT INTO identica_account_reservations (reservation_key, unique_id, created_at, expires_at) VALUES ('alice', " + ACCOUNT + ", 1, 2)",
				"INSERT INTO identica_account_username_state (unique_id, source, updated_at) VALUES (" + ACCOUNT + ", 'PROVIDER', 3)",
				"INSERT INTO identica_account_username_history (unique_id, provider_id, old_username, new_username, source, changed_at) VALUES (" + ACCOUNT + ", NULL, 'Alice', 'Alicia', 'PROVIDER', 4)"
		);
	}

	/**
	 * Development builds left the username history index out on SQLite; version 2 adds it.
	 */
	@Override
	protected SchemaSnapshot upgraded(SchemaSnapshot developmentBuild) {
		return developmentBuild.withIndex("identica_account_username_history", "unique_id", "changed_at");
	}

	@Test
	void treatsADatabaseWithoutAccountsAsNew() throws Exception {
		assertEquals(OptionalInt.empty(), baseline());
	}

	@Test
	void startsTheUsernameLayoutOf221AtZero() throws Exception {
		assertEquals(OptionalInt.of(0), baseline(
				"CREATE TABLE identica_accounts (unique_id UUID PRIMARY KEY, username VARCHAR(64) NOT NULL, username_source VARCHAR(32) NOT NULL)"));
		assertEquals(OptionalInt.of(0), baseline(
				"CREATE TABLE identica_accounts (unique_id UUID PRIMARY KEY, username VARCHAR(64) NOT NULL)",
				"CREATE TABLE identica_username_history (unique_id UUID NOT NULL)"));
	}

	@Test
	void placesTheLayoutOfADevelopmentBuildAtOne() throws Exception {
		assertEquals(OptionalInt.of(1), baseline(
				"CREATE TABLE identica_accounts (unique_id UUID PRIMARY KEY, username VARCHAR(64) NOT NULL)",
				"CREATE TABLE identica_account_username_state (unique_id UUID PRIMARY KEY)"));
	}

	private static OptionalInt baseline(String... statements) throws Exception {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:account_baseline_" + UUID.randomUUID());
		try (Connection connection = dataSource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				for (String sql : statements) statement.execute(sql);
			}

			return new AccountSchema().baseline(new MetadataContext(connection), 2);
		}
	}

	/**
	 * Answers the baseline's questions from the connection's metadata, as Strata's database
	 * context does.
	 */
	private record MetadataContext(Connection connection) implements DatabaseContext {
		@Override
		public @NotNull Connection getConnection() {
			return connection;
		}

		@Override
		public @NotNull String getDialect() throws SQLException {
			return connection.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT);
		}

		@Override
		public void execute(@NotNull String sql) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int update(@NotNull String sql, Object... parameters) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean tableExists(@NotNull String table) throws SQLException {
			DatabaseMetaData metaData = connection.getMetaData();
			try (ResultSet tables = metaData.getTables(null, null, table.toUpperCase(Locale.ROOT), null)) {
				return tables.next();
			}
		}

		@Override
		public boolean columnExists(@NotNull String table, @NotNull String column) throws SQLException {
			DatabaseMetaData metaData = connection.getMetaData();
			try (ResultSet columns = metaData.getColumns(null, null, table.toUpperCase(Locale.ROOT), column.toUpperCase(Locale.ROOT))) {
				return columns.next();
			}
		}
	}
}
