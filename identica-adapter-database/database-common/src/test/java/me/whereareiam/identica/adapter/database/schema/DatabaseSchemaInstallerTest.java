package me.whereareiam.identica.adapter.database.schema;

import me.whereareiam.dialectica.schema.ColumnDefinition;
import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.dialectica.schema.TableDefinition;
import me.whereareiam.dialectica.schema.type.ColumnType;
import me.whereareiam.identica.database.schema.DatabaseSchema;
import me.whereareiam.strata.Migration;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseSchemaInstallerTest {
	private Jdbi jdbi;

	@BeforeEach
	void openDatabase() {
		jdbi = Jdbi.create("jdbc:h2:mem:installer_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
	}

	@Test
	void createsTheSchemaOfANewInstallationAndRecordsItsLatestVersion() {
		new DatabaseSchemaInstaller(jdbi).install(new TestSchema("test/core", "test_core", Set.of()));

		assertTrue(tableExists("test_core"));
		assertEquals(List.of("2 creation"), history("test/core"));
	}

	@Test
	void installsASchemaAgainWithoutChangingAnything() {
		new DatabaseSchemaInstaller(jdbi).install(new TestSchema("test/core", "test_core", Set.of()));

		DatabaseSchemaInstaller nextRun = new DatabaseSchemaInstaller(jdbi);
		nextRun.install(new TestSchema("test/core", "test_core", Set.of()));
		nextRun.install(new TestSchema("test/core", "test_core", Set.of()));

		assertEquals(List.of("2 creation"), history("test/core"));
	}

	@Test
	void installsASchemaAfterTheStreamsItRequires() {
		DatabaseSchemaInstaller installer = new DatabaseSchemaInstaller(jdbi);
		installer.install(new TestSchema("test/core", "test_core", Set.of()));

		installer.install(new TestSchema("test/addon", "test_addon", Set.of("test/core")));

		assertTrue(tableExists("test_addon"));
	}

	@Test
	void refusesASchemaWhoseRequiredStreamsThisRunHasNotInstalled() {
		new DatabaseSchemaInstaller(jdbi).install(new TestSchema("test/core", "test_core", Set.of()));
		DatabaseSchemaInstaller nextRun = new DatabaseSchemaInstaller(jdbi);

		IllegalStateException failure = assertThrows(IllegalStateException.class,
				() -> nextRun.install(new TestSchema("test/addon", "test_addon", Set.of("test/core"))));

		assertEquals("Schema test/addon requires test/core, which has to be installed before it", failure.getMessage());
		assertFalse(tableExists("test_addon"));
	}

	private boolean tableExists(String table) {
		return jdbi.withHandle(handle -> handle.createQuery(
						"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = :table")
				.bind("table", table.toUpperCase())
				.mapTo(Long.class)
				.one() > 0);
	}

	private List<String> history(String stream) {
		return jdbi.withHandle(handle -> handle.createQuery("SELECT version, name FROM strata_history WHERE stream = :stream ORDER BY version")
				.bind("stream", stream)
				.map((row, context) -> row.getInt("version") + " " + row.getString("name"))
				.list());
	}

	private record TestSchema(String stream, String table, Set<String> requires) implements DatabaseSchema {
		@Override
		public @NotNull Schema schema() {
			return Schema.of(TableDefinition.builder(table)
					.column(ColumnDefinition.builder("id", ColumnType.bigint()).notNull().build())
					.primaryKey("id")
					.build());
		}

		@Override
		public @NotNull List<Migration<DatabaseContext>> migrations() {
			return List.of(new Migration<>(1, "first", database -> {}), new Migration<>(2, "second", database -> {}));
		}
	}
}
