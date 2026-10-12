package me.whereareiam.identica.adapter.database.mapper.account;

import me.whereareiam.identica.adapter.database.entity.account.AccountEntity;
import me.whereareiam.identica.model.identity.Account;

import java.util.Objects;

public final class AccountMapper {
	public static Account toModel(AccountEntity entity) {
		if (entity == null) return null;
		return Account.builder()
				.uniqueId(entity.getUniqueId())
				.username(entity.getUsername())
				.createdAt(Objects.requireNonNullElse(entity.getCreatedAt(), 0L))
				.lastSeenAt(Objects.requireNonNullElse(entity.getLastSeenAt(), 0L))
				.build();
	}

	public static AccountEntity toEntity(Account account) {
		if (account == null) return null;
		return AccountEntity.builder()
				.uniqueId(account.getUniqueId())
				.username(account.getUsername())
				.createdAt(account.getCreatedAt())
				.lastSeenAt(account.getLastSeenAt())
				.build();
	}
}
