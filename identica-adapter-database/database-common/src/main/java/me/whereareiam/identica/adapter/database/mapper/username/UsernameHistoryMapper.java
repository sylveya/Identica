package me.whereareiam.identica.adapter.database.mapper.username;

import me.whereareiam.identica.adapter.database.entity.username.UsernameHistoryEntity;
import me.whereareiam.identica.model.identity.username.AccountUsernameHistoryEntry;

public final class UsernameHistoryMapper {
	public static UsernameHistoryEntity toEntity(AccountUsernameHistoryEntry entry) {
		if (entry == null) return null;
		return UsernameHistoryEntity.builder()
				.uniqueId(entry.getUniqueId())
				.providerId(entry.getProviderId())
				.oldUsername(entry.getOldUsername())
				.newUsername(entry.getNewUsername())
				.source(entry.getSource().getId())
				.changedAt(entry.getChangedAt())
				.build();
	}
}
