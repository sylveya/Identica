package me.whereareiam.identica.adapter.database.entity.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.dialectica.entity.annotation.Column;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Nullable;
import me.whereareiam.dialectica.entity.annotation.References;
import me.whereareiam.dialectica.entity.annotation.Table;
import me.whereareiam.dialectica.entity.annotation.Unique;
import me.whereareiam.dialectica.schema.type.ReferentialAction;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("identica_provider_links")
@Unique({"uniqueId", "providerId"})
public class AccountProviderLinkEntity {
	@Id
	@Length(64)
	private String providerId;
	@Id
	@Length(128)
	private String providerSubject;
	@References(entity = AccountEntity.class, onDelete = ReferentialAction.CASCADE)
	private UUID uniqueId;
	@Column("is_primary")
	private boolean primaryLink;
	@Nullable
	private Long linkedAt;
	@Nullable
	private Long lastSeenAt;
}
