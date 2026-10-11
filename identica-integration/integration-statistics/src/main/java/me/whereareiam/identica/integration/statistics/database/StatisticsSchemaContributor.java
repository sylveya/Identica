package me.whereareiam.identica.integration.statistics.database;

import me.whereareiam.dialectica.SchemaManager;
import me.whereareiam.identica.database.schema.SchemaContributor;
import org.jetbrains.annotations.NotNull;

public final class StatisticsSchemaContributor implements SchemaContributor {
	@Override
	public void contribute(@NotNull SchemaManager schemaManager) {
		schemaManager.scanPackages("me.whereareiam.identica.integration.statistics.database");
	}
}
