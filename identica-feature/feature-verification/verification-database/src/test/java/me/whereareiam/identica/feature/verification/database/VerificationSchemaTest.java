package me.whereareiam.identica.feature.verification.database;

import me.whereareiam.identica.adapter.database.testing.schema.DatabaseSchemaContract;
import me.whereareiam.identica.database.schema.DatabaseSchema;

import java.util.List;

class VerificationSchemaTest extends DatabaseSchemaContract {
	private static final String ACCOUNT = "'00000000-0000-0000-0000-000000000001'";

	@Override
	protected DatabaseSchema schema() {
		return new VerificationSchema();
	}

	@Override
	protected List<String> developmentBuild() {
		return List.of("account", "verification");
	}

	@Override
	protected List<String> tables() {
		return List.of(
				"identica_verification_enrollments",
				"identica_verification_recovery_codes",
				"identica_verification_selections"
		);
	}

	@Override
	protected List<String> rows() {
		return List.of(
				"INSERT INTO identica_accounts (unique_id, username, created_at, last_seen_at) VALUES (" + ACCOUNT + ", 'Alice', 1, 2)",
				"INSERT INTO identica_verification_enrollments (unique_id, method_id, enrollment_id, credential, label, created_at, enabled_at) VALUES (" + ACCOUNT + ", 'totp', 'phone', 'secret', NULL, 1, 2)",
				"INSERT INTO identica_verification_recovery_codes (unique_id, method_id, code_hash, created_at, used_at) VALUES (" + ACCOUNT + ", 'totp', 'hash', 1, NULL)",
				"INSERT INTO identica_verification_selections (unique_id, provider_id, method_id, selected_at) VALUES (" + ACCOUNT + ", 'credential', 'totp', 1)"
		);
	}
}
