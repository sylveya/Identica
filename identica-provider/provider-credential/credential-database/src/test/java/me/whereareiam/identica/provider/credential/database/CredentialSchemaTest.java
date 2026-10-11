package me.whereareiam.identica.provider.credential.database;

import me.whereareiam.identica.adapter.database.testing.schema.DatabaseSchemaContract;
import me.whereareiam.identica.database.schema.DatabaseSchema;

import java.util.List;

class CredentialSchemaTest extends DatabaseSchemaContract {
	@Override
	protected DatabaseSchema schema() {
		return new CredentialSchema();
	}

	@Override
	protected List<String> developmentBuild() {
		return List.of("account", "credential");
	}

	@Override
	protected List<String> tables() {
		return List.of(
				"identica_provider_credential_accounts",
				"identica_provider_credential_accounts_history"
		);
	}

	@Override
	protected List<String> rows() {
		return List.of(
				"INSERT INTO identica_provider_credential_accounts (provider_id, provider_subject, password_hash, hashing_method, created_at, updated_at) VALUES ('credential', 'alice', 'hash', 'ARGON2', 1, NULL)",
				"INSERT INTO identica_provider_credential_accounts_history (provider_id, provider_subject, hashing_method, change_reason, changed_at) VALUES ('credential', 'alice', 'ARGON2', 'REGISTER', 1)"
		);
	}
}
