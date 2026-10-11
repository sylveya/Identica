package me.whereareiam.identica.feature.verification.database.mapper;

import me.whereareiam.identica.feature.verification.database.entity.VerificationSelectionEntity;
import me.whereareiam.identica.feature.verification.model.selection.VerificationSelection;

import java.util.Objects;

public final class VerificationSelectionMapper {
	public static VerificationSelection toModel(VerificationSelectionEntity entity) {
		return VerificationSelection.builder()
				.uniqueId(entity.getUniqueId())
				.providerId(entity.getProviderId())
				.methodId(entity.getMethodId())
				.selectedAt(Objects.requireNonNullElse(entity.getSelectedAt(), 0L))
				.build();
	}

	public static VerificationSelectionEntity toEntity(VerificationSelection model) {
		return VerificationSelectionEntity.builder()
				.uniqueId(model.getUniqueId())
				.providerId(model.getProviderId())
				.methodId(model.getMethodId())
				.selectedAt(model.getSelectedAt())
				.build();
	}
}
