package me.whereareiam.identica.provider.credential.database;

import me.whereareiam.dialectica.Dialectica;
import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.identica.database.schema.DatabaseSchema;
import me.whereareiam.identica.provider.credential.database.entity.CredentialAccountEntity;
import me.whereareiam.identica.provider.credential.database.entity.CredentialAccountHistoryEntity;
import me.whereareiam.strata.Migration;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.OptionalInt;

/**
 * The tables of the credential provider: the password of each provider subject and the history of
 * its changes. A database that has the credential accounts table already has version 1, the layout
 * the provider created before migrations.
 */
public final class CredentialSchema implements DatabaseSchema {
	/** Name Strata tracks the credential tables under; it never changes. */
	public static final String STREAM = "identica/provider/credential";

	private static final List<Migration<DatabaseContext>> MIGRATIONS = List.of(
			// Changes nothing: version 1 is the layout the provider created before migrations existed.
			new Migration<>(1, "initial-layout", database -> {})
	);

	@Override
	public @NotNull String stream() {
		return STREAM;
	}

	@Override
	public @NotNull Schema schema() {
		return Schema.of(
				Dialectica.entity(CredentialAccountEntity.class).table(),
				Dialectica.entity(CredentialAccountHistoryEntity.class).table()
		);
	}

	@Override
	public @NotNull List<Migration<DatabaseContext>> migrations() {
		return MIGRATIONS;
	}

	@Override
	public @NotNull OptionalInt baseline(@NotNull DatabaseContext database, int latest) throws SQLException {
		return database.tableExists("identica_provider_credential_accounts") ? OptionalInt.of(1) : OptionalInt.empty();
	}
}
