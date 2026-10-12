package me.whereareiam.identica.database.schema;

import me.whereareiam.dialectica.schema.Schema;
import me.whereareiam.strata.Migration;
import me.whereareiam.strata.adapter.database.DatabaseContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

/**
 * The tables one owner keeps in Identica's database, and how an installation of them is upgraded.
 *
 * <p>Each owner (the core, a feature, a provider or an add-on) declares one schema and hands it to
 * the {@link SchemaInstaller} when it loads. The installer runs it as a Strata migration stream named
 * {@link #stream()}: a new installation gets {@link #schema()} in its newest form, and an existing one
 * runs the {@link #migrations()} it has not seen yet. Strata records the stream's version in the
 * {@code strata_history} table, and holds a lock on PostgreSQL, MySQL and MariaDB, so proxies that
 * share a database install a schema once.
 *
 * <p>Example:
 * <pre>{@code
 * public final class ExampleSchema implements DatabaseSchema {
 *     public static final String STREAM = "example/addon";
 *
 *     @Override
 *     public @NotNull String stream() {
 *         return STREAM;
 *     }
 *
 *     @Override
 *     public @NotNull Schema schema() {
 *         return Schema.of(Dialectica.entity(ExampleEntity.class).table());
 *     }
 *
 *     @Override
 *     public @NotNull Set<String> requires() {
 *         return Set.of(DatabaseSchema.ACCOUNT_STREAM);
 *     }
 * }
 * }</pre>
 *
 * <p>The schema describes what the last migration leaves behind. Once released, a migration's
 * version and what it does never change; a new one is appended with the next version.
 */
public interface DatabaseSchema {
	/**
	 * Stream of the core schema: accounts, provider links and profiles, reservations and
	 * usernames. A schema whose tables reference an {@link AccountTable account} requires it.
	 */
	String ACCOUNT_STREAM = "identica/account";


	/**
	 * Returns the name Strata tracks this schema under. It is made of lowercase segments separated
	 * by slashes, such as {@code identica/feature/verification}, and never changes.
	 *
	 * @return the stream name
	 */
	@NotNull String stream();

	/**
	 * Returns the tables in their newest form, as a new installation gets them. Tables of another
	 * owner that they reference belong to a stream listed in {@link #requires()}.
	 *
	 * @return the schema
	 */
	@NotNull Schema schema();

	/**
	 * Returns the numbered changes that bring an older installation up to {@link #schema()}, in
	 * order, starting at version 1 without gaps.
	 *
	 * @return the migrations; empty by default
	 */
	default @NotNull List<Migration<DatabaseContext>> migrations() {
		return List.of();
	}

	/**
	 * Tells where an installation stands that Strata has no version for, which is the case the first
	 * time this stream runs against a database.
	 *
	 * <p>Return {@link OptionalInt#empty()} for a new installation, which gets {@link #schema()} and is
	 * recorded at the latest version; {@code 0} for tables that predate every migration, which then all
	 * run; or the version whose layout the tables already have. The default answers empty, so every
	 * database the stream has not seen counts as new; creating the schema skips tables that exist.
	 *
	 * @param database the database, under the stream's lock
	 * @param latest   the version of the last migration
	 * @return the version the installation is at, or empty for a new installation
	 * @throws Exception when the database cannot be inspected
	 */
	default @NotNull OptionalInt baseline(@NotNull DatabaseContext database, int latest) throws Exception {
		return OptionalInt.empty();
	}

	/**
	 * Returns the streams that have to be installed before this one in the same run, typically
	 * because this schema's tables reference theirs, such as {@link #ACCOUNT_STREAM}.
	 *
	 * @return the required stream names; empty by default
	 */
	default @NotNull Set<String> requires() {
		return Set.of();
	}
}
