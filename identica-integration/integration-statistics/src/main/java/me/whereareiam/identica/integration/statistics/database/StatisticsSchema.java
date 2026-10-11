package me.whereareiam.identica.integration.statistics.database;

import me.whereareiam.dialectica.Dialectica;
import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.identica.database.schema.DatabaseSchema;
import me.whereareiam.strata.Migration;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.OptionalInt;

/**
 * The table of the usage statistics: the lease that elects the proxy sending shared metrics. A
 * database that has the lease table already has version 1, the layout the statistics created
 * before migrations.
 */
public final class StatisticsSchema implements DatabaseSchema {
	/** Name Strata tracks the statistics table under; it never changes. */
	public static final String STREAM = "identica/integration/statistics";

	private static final List<Migration<DatabaseContext>> MIGRATIONS = List.of(
			// Changes nothing: version 1 is the layout the statistics created before migrations existed.
			new Migration<>(1, "initial-layout", database -> {})
	);

	@Override
	public @NotNull String stream() {
		return STREAM;
	}

	@Override
	public @NotNull Schema schema() {
		return Schema.of(Dialectica.entity(StatisticsLeaseEntity.class).table());
	}

	@Override
	public @NotNull List<Migration<DatabaseContext>> migrations() {
		return MIGRATIONS;
	}

	@Override
	public @NotNull OptionalInt baseline(@NotNull DatabaseContext database, int latest) throws SQLException {
		return database.tableExists(StatisticsLeaseEntity.TABLE) ? OptionalInt.of(1) : OptionalInt.empty();
	}
}
