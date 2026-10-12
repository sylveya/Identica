package me.whereareiam.identica.database;

/**
 * Identica's database: the connection it opens on startup to the database {@code persistence}
 * configures, and closes on shutdown.
 */
public interface DatabaseService {
	/**
	 * Tells whether the database is open and its core schema installed.
	 *
	 * @return {@code true} between a successful startup and shutdown
	 */
	boolean isInitialized();
}
