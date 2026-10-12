package me.whereareiam.identica.adapter.database.entity.username;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
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
@Table("identica_account_username_state")
public class UsernameStateEntity {
	@Id
	@References(entity = AccountEntity.class, onDelete = ReferentialAction.CASCADE)
	private UUID uniqueId;
	@Length(32)
	private String source;
	private long updatedAt;
}
