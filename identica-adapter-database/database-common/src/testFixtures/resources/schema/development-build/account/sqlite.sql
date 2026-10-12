-- The account tables a development build of Identica 3.0.0 created on SQLITE.
-- Generated from the hand-written DDL of commit 13af5c2f684c1d824a8ea06f01186a0b941cdd36 (origin/dev).
-- Dialectica 1.0.2 ran each block below as one JDBC statement; run them the same way.
-- statement
CREATE TABLE IF NOT EXISTS identica_accounts (
	unique_id TEXT PRIMARY KEY,
	username VARCHAR(64) NOT NULL,
	created_at INTEGER,
	last_seen_at INTEGER
)
-- statement
CREATE TABLE IF NOT EXISTS identica_provider_links (
	provider_id VARCHAR(64) NOT NULL,
	provider_subject VARCHAR(128) NOT NULL,
	unique_id TEXT NOT NULL,
	is_primary INTEGER NOT NULL,
	linked_at INTEGER,
	last_seen_at INTEGER,
	PRIMARY KEY (provider_id, provider_subject),
	UNIQUE (unique_id, provider_id),
	FOREIGN KEY (unique_id) REFERENCES identica_accounts(unique_id) ON DELETE CASCADE
)
-- statement
CREATE TABLE IF NOT EXISTS identica_provider_profiles (
	provider_id VARCHAR(64) NOT NULL,
	provider_subject VARCHAR(128) NOT NULL,
	provider_username VARCHAR(64) NOT NULL,
	PRIMARY KEY (provider_id, provider_subject),
	FOREIGN KEY (provider_id, provider_subject) REFERENCES identica_provider_links(provider_id, provider_subject) ON DELETE CASCADE
)
-- statement
CREATE TABLE IF NOT EXISTS identica_account_reservations (
	reservation_key VARCHAR(192) PRIMARY KEY,
	unique_id TEXT NOT NULL,
	created_at INTEGER NOT NULL,
	expires_at INTEGER NOT NULL
)
-- statement
CREATE TABLE IF NOT EXISTS identica_account_username_state (
	unique_id TEXT NOT NULL,
	source VARCHAR(32) NOT NULL,
	updated_at INTEGER NOT NULL,
	PRIMARY KEY (unique_id),
	FOREIGN KEY (unique_id)
		REFERENCES identica_accounts(unique_id)
		ON DELETE CASCADE
)
-- statement
CREATE TABLE IF NOT EXISTS identica_account_username_history (
	unique_id TEXT NOT NULL,
	provider_id VARCHAR(64),
	old_username VARCHAR(64) NOT NULL,
	new_username VARCHAR(64) NOT NULL,
	source VARCHAR(32) NOT NULL,
	changed_at INTEGER NOT NULL,
	FOREIGN KEY (unique_id)
		REFERENCES identica_accounts(unique_id)
		ON DELETE CASCADE
)
;
CREATE INDEX IF NOT EXISTS identica_account_username_history_unique_id_changed_at_idx
	ON identica_account_username_history (unique_id, changed_at)
