package me.whereareiam.identica.adapter.database.schema;

import me.whereareiam.identica.database.schema.DatabaseSchema;
import me.whereareiam.identica.database.schema.SchemaInstaller;
import me.whereareiam.identica.logging.Logger;
import me.whereareiam.strata.MigrationStream;
import me.whereareiam.strata.Strata;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import me.whereareiam.strata.adapter.database.DatabaseTarget;
import me.whereareiam.strata.adapter.database.dialectica.SchemaCreation;
import me.whereareiam.strata.adapter.database.jdbi.JdbiConnector;
import me.whereareiam.strata.model.AppliedMigration;
import me.whereareiam.strata.model.MigrationReport;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Installs each owner's {@link DatabaseSchema} as a Strata migration stream on Identica's Jdbi, and
 * remembers which streams this run has installed, so that a schema cannot be installed before the
 * streams it requires.
 */
public final class DatabaseSchemaInstaller implements SchemaInstaller {
	private final Jdbi jdbi;
	private final Set<String> installed = new LinkedHashSet<>();

	/**
	 * @param jdbi Identica's Jdbi; migrations run on its handles, so they see its plugins and mappers
	 */
	public DatabaseSchemaInstaller(@NotNull Jdbi jdbi) {
		this.jdbi = Objects.requireNonNull(jdbi, "jdbi");
	}

	@Override
	public synchronized void install(@NotNull DatabaseSchema schema) {
		String stream = schema.stream();
		Set<String> missing = new LinkedHashSet<>(schema.requires());
		missing.removeAll(installed);
		if (!missing.isEmpty())
			throw new IllegalStateException("Schema " + stream + " requires " + String.join(", ", missing)
					+ ", which has to be installed before it");

		MigrationStream<DatabaseContext> migrations = MigrationStream.<DatabaseContext>builder()
				.id(stream)
				.target(new DatabaseTarget(new JdbiConnector(jdbi)))
				.baseline(schema::baseline)
				.creation(SchemaCreation.of(schema.schema()))
				.migrations(schema.migrations())
				.build();
		MigrationReport report = new Strata(List.of(migrations)).migrate();
		for (AppliedMigration applied : report.getApplied().getOrDefault(stream, List.of()))
			Logger.info("Database schema %s: applied %d %s", stream, applied.getVersion(), applied.getName());

		installed.add(stream);
	}
}
