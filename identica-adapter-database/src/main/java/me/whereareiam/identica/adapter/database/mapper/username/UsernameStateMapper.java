package me.whereareiam.identica.adapter.database.mapper.username;

import me.whereareiam.identica.adapter.database.entity.username.UsernameStateEntity;
import me.whereareiam.identica.model.identity.username.AccountUsernameState;
import me.whereareiam.identica.type.identity.AccountUsernameSource;

public final class UsernameStateMapper {
	public static AccountUsernameState toModel(UsernameStateEntity entity) {
		if (entity == null) return null;
		return AccountUsernameState.builder()
				.uniqueId(entity.getUniqueId())
				.source(AccountUsernameSource.fromId(entity.getSource()))
				.updatedAt(entity.getUpdatedAt())
				.build();
	}
}
