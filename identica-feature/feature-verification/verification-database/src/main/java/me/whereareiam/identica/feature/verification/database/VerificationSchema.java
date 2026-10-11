package me.whereareiam.identica.feature.verification.database;

import me.whereareiam.dialectica.Dialectica;
import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.identica.database.schema.DatabaseSchema;
import me.whereareiam.identica.feature.verification.database.entity.VerificationEnrollmentEntity;
import me.whereareiam.identica.feature.verification.database.entity.VerificationRecoveryCodeEntity;
import me.whereareiam.identica.feature.verification.database.entity.VerificationSelectionEntity;
import me.whereareiam.strata.Migration;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

/**
 * The tables of the verification feature: enrollments, recovery codes and the method selected per
 * provider, each belonging to an account. A database that has the enrollments table already has
 * version 1, the layout the feature created before migrations.
 */
public final class VerificationSchema implements DatabaseSchema {
	/** Name Strata tracks the verification tables under; it never changes. */
	public static final String STREAM = "identica/feature/verification";

	private static final List<Migration<DatabaseContext>> MIGRATIONS = List.of(
			// Changes nothing: version 1 is the layout the feature created before migrations existed.
			new Migration<>(1, "initial-layout", database -> {})
	);

	@Override
	public @NotNull String stream() {
		return STREAM;
	}

	@Override
	public @NotNull Schema schema() {
		return Schema.of(
				Dialectica.entity(VerificationEnrollmentEntity.class).table(),
				Dialectica.entity(VerificationRecoveryCodeEntity.class).table(),
				Dialectica.entity(VerificationSelectionEntity.class).table()
		);
	}

	@Override
	public @NotNull List<Migration<DatabaseContext>> migrations() {
		return MIGRATIONS;
	}

	@Override
	public @NotNull OptionalInt baseline(@NotNull DatabaseContext database, int latest) throws SQLException {
		return database.tableExists("identica_verification_enrollments") ? OptionalInt.of(1) : OptionalInt.empty();
	}

	@Override
	public @NotNull Set<String> requires() {
		return Set.of(ACCOUNT_STREAM);
	}
}
