package me.whereareiam.identica.provider.credential.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.dialectica.entity.annotation.ForeignKey;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Table;
import me.whereareiam.dialectica.schema.type.ReferentialAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("identica_provider_credential_accounts_history")
@ForeignKey(fields = {"providerId", "providerSubject"}, entity = CredentialAccountEntity.class, onDelete = ReferentialAction.CASCADE)
public class CredentialAccountHistoryEntity {
	@Id
	@Length(64)
	private String providerId;
	@Id
	@Length(128)
	private String providerSubject;
	@Length(64)
	private String hashingMethod;
	@Length(32)
	private String changeReason;
	@Id
	private long changedAt;
}
