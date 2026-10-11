package me.whereareiam.identica.feature.verification.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Nullable;
import me.whereareiam.dialectica.entity.annotation.References;
import me.whereareiam.dialectica.entity.annotation.Table;
import me.whereareiam.dialectica.schema.type.ReferentialAction;
import me.whereareiam.identica.database.schema.AccountTable;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("identica_verification_recovery_codes")
public class VerificationRecoveryCodeEntity {
	@Id
	@References(table = AccountTable.NAME, column = AccountTable.KEY, onDelete = ReferentialAction.CASCADE)
	private UUID uniqueId;
	@Id
	@Length(64)
	private String methodId;
	@Id
	@Length(128)
	private String codeHash;
	@Nullable
	private Long createdAt;
	@Nullable
	private Long usedAt;
}
