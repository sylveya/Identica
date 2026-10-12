package me.whereareiam.identica.adapter.database.schema;

import me.whereareiam.dialectica.Dialectica;
import me.whereareiam.dialectica.schema.IndexDefinition;
import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.dialectica.schema.change.CreateIndex;
import me.whereareiam.identica.adapter.database.entity.account.AccountEntity;
import me.whereareiam.identica.adapter.database.entity.account.AccountProviderLinkEntity;
import me.whereareiam.identica.adapter.database.entity.account.AccountProviderProfileEntity;
import me.whereareiam.identica.adapter.database.entity.account.AccountReservationEntity;
import me.whereareiam.identica.adapter.database.entity.username.UsernameHistoryEntity;
import me.whereareiam.identica.adapter.database.entity.username.UsernameStateEntity;
import me.whereareiam.identica.database.schema.AccountTable;
import me.whereareiam.identica.database.schema.DatabaseSchema;
import me.whereareiam.strata.Migration;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import me.whereareiam.strata.adapter.database.dialectica.SchemaMigration;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.OptionalInt;

/**
 * The core tables: accounts, their provider links and profiles, username reservations, and the
 * username state and history of an account.
 *
 * <p>Version 1 is the layout of the development builds before migrations, and version 2 adds the
 * username history index that those builds left out on SQLite. A database without
 * {@code identica_accounts} is a new installation. One that still has the 2.2.1 username layout,
 * {@code identica_accounts.username_source} or {@code identica_username_history}, starts at 0;
 * any other one is at version 1.
 */
public final class AccountSchema implements DatabaseSchema {
	private static final List<Migration<DatabaseContext>> MIGRATIONS = List.of(
			// Changes nothing: version 1 is the layout development builds had before migrations existed.
			new Migration<>(1, "initial-layout", database -> {}),
			// Development builds created the history table without its index on SQLite.
			SchemaMigration.of(2, "username-history-index", new CreateIndex("identica_account_username_history",
					IndexDefinition.plain("identica_account_username_history_unique_id_changed_at_idx", "unique_id", "changed_at")))
	);

	@Override
	public @NotNull String stream() {
		return ACCOUNT_STREAM;
	}

	@Override
	public @NotNull Schema schema() {
		return Schema.of(
				Dialectica.entity(AccountEntity.class).table(),
				Dialectica.entity(AccountProviderLinkEntity.class).table(),
				Dialectica.entity(AccountProviderProfileEntity.class).table(),
				Dialectica.entity(AccountReservationEntity.class).table(),
				Dialectica.entity(UsernameStateEntity.class).table(),
				Dialectica.entity(UsernameHistoryEntity.class).table()
		);
	}

	@Override
	public @NotNull List<Migration<DatabaseContext>> migrations() {
		return MIGRATIONS;
	}

	@Override
	public @NotNull OptionalInt baseline(@NotNull DatabaseContext database, int latest) throws SQLException {
		if (!database.tableExists(AccountTable.NAME)) return OptionalInt.empty();
		if (database.columnExists(AccountTable.NAME, "username_source") || database.tableExists("identica_username_history"))
			return OptionalInt.of(0);

		return OptionalInt.of(1);
	}
}
