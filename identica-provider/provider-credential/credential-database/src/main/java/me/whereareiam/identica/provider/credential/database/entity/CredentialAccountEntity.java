package me.whereareiam.identica.provider.credential.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Nullable;
import me.whereareiam.dialectica.entity.annotation.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("identica_provider_credential_accounts")
public class CredentialAccountEntity {
	@Id
	@Length(64)
	private String providerId;
	@Id
	@Length(128)
	private String providerSubject;
	@Length(512)
	private String passwordHash;
	@Length(64)
	private String hashingMethod;
	@Nullable
	private Long createdAt;
	@Nullable
	private Long updatedAt;
}
