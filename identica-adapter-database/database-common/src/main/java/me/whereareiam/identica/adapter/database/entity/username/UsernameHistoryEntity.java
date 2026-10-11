package me.whereareiam.identica.adapter.database.entity.username;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.dialectica.entity.annotation.Index;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Nullable;
import me.whereareiam.dialectica.entity.annotation.References;
import me.whereareiam.dialectica.entity.annotation.Table;
import me.whereareiam.dialectica.schema.type.ReferentialAction;
import me.whereareiam.identica.adapter.database.entity.account.AccountEntity;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("identica_account_username_history")
@Index(name = "identica_account_username_history_unique_id_changed_at_idx", columns = {"uniqueId", "changedAt"})
public class UsernameHistoryEntity {
	@References(entity = AccountEntity.class, onDelete = ReferentialAction.CASCADE)
	private UUID uniqueId;
	@Nullable
	@Length(64)
	private String providerId;
	@Length(64)
	private String oldUsername;
	@Length(64)
	private String newUsername;
	@Length(32)
	private String source;
	private long changedAt;
}
