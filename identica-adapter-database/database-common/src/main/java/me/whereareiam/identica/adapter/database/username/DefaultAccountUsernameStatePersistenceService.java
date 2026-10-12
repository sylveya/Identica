package me.whereareiam.identica.adapter.database.username;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.identica.event.EventListener;
import me.whereareiam.identica.event.EventManager;
import me.whereareiam.identica.event.account.AccountLifecycleEvent;
import me.whereareiam.identica.event.base.IdenticEvent;
import me.whereareiam.identica.persistence.username.AccountUsernameStatePersistenceService;
import me.whereareiam.identica.adapter.database.mapper.username.UsernameStateMapper;
import me.whereareiam.identica.adapter.database.repository.username.UsernameStateRepository;
import me.whereareiam.identica.model.identity.username.AccountUsernameState;
import me.whereareiam.identica.type.identity.AccountUsernameSource;
import me.whereareiam.identica.type.event.EventOrder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultAccountUsernameStatePersistenceService implements AccountUsernameStatePersistenceService, EventListener {
	private final UsernameStateRepository repository;
	private final EventManager eventManager;

	@Inject
	void registerListeners() {
		eventManager.register(this);
	}

	@Override
	public @NotNull Optional<AccountUsernameState> find(@NotNull UUID uniqueId) {
		return repository.findByUniqueId(uniqueId).map(UsernameStateMapper::toModel);
	}

	@Override
	public void save(@NotNull UUID uniqueId, @NotNull AccountUsernameSource source) {
		long now = System.currentTimeMillis();
		if (repository.findByUniqueId(uniqueId).isPresent()) {
			repository.update(uniqueId, source.getId(), now);
			return;
		}

		repository.insert(uniqueId, source.getId(), now);
	}

	@Override
	public void delete(@NotNull UUID uniqueId) {
		repository.delete(uniqueId);
	}

	@IdenticEvent(EventOrder.HIGH)
	public void onAccountLifecycle(@NotNull AccountLifecycleEvent event) {
		UUID uniqueId = event.getIdentity().getAccountUniqueId();
		if (uniqueId == null) return;

		delete(uniqueId);
	}
}
