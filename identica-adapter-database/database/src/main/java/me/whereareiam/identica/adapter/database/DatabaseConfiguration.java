package me.whereareiam.identica.adapter.database;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import me.whereareiam.identica.adapter.database.account.DefaultAccountPersistenceService;
import me.whereareiam.identica.adapter.database.account.DefaultAccountReservationPersistenceService;
import me.whereareiam.identica.adapter.database.provider.DefaultProviderLinkPersistenceService;
import me.whereareiam.identica.adapter.database.provider.DefaultProviderProfilePersistenceService;
import me.whereareiam.identica.adapter.database.repository.account.AccountRepository;
import me.whereareiam.identica.adapter.database.repository.account.AccountReservationRepository;
import me.whereareiam.identica.adapter.database.repository.provider.ProviderLinkRepository;
import me.whereareiam.identica.adapter.database.repository.provider.ProviderProfileRepository;
import me.whereareiam.identica.adapter.database.repository.username.UsernameHistoryRepository;
import me.whereareiam.identica.adapter.database.repository.username.UsernameStateRepository;
import me.whereareiam.identica.adapter.database.username.DefaultAccountUsernameHistoryPersistenceService;
import me.whereareiam.identica.adapter.database.username.DefaultAccountUsernameStatePersistenceService;
import me.whereareiam.identica.database.AccountPersistenceService;
import me.whereareiam.identica.database.AccountReservationPersistenceService;
import me.whereareiam.identica.database.DatabaseService;
import me.whereareiam.identica.database.provider.ProviderLinkPersistenceService;
import me.whereareiam.identica.database.provider.ProviderProfilePersistenceService;
import me.whereareiam.identica.database.schema.SchemaInstaller;
import me.whereareiam.identica.database.username.AccountUsernameHistoryPersistenceService;
import me.whereareiam.identica.database.username.AccountUsernameStatePersistenceService;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

public class DatabaseConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(Jdbi.class).toProvider(JdbiProvider.class);
		bind(DatabaseService.class).to(DefaultDatabaseService.class).asEagerSingleton();
		bind(AccountPersistenceService.class).to(DefaultAccountPersistenceService.class).asEagerSingleton();
		bind(AccountReservationPersistenceService.class).to(DefaultAccountReservationPersistenceService.class).asEagerSingleton();
		bind(ProviderLinkPersistenceService.class).to(DefaultProviderLinkPersistenceService.class).asEagerSingleton();
		bind(ProviderProfilePersistenceService.class).to(DefaultProviderProfilePersistenceService.class).asEagerSingleton();
		bind(AccountUsernameStatePersistenceService.class).to(DefaultAccountUsernameStatePersistenceService.class).asEagerSingleton();
		bind(AccountUsernameHistoryPersistenceService.class).to(DefaultAccountUsernameHistoryPersistenceService.class).asEagerSingleton();
	}

	@Provides
	public DataSource provideDataSource(DefaultDatabaseService databaseService) {
		return databaseService.getDataSource();
	}

	@Provides
	@Singleton
	public SchemaInstaller provideSchemaInstaller(DefaultDatabaseService databaseService) {
		return databaseService.getSchemaInstaller();
	}

	@Provides
	@Singleton
	public AccountRepository provideAccountRepository(Jdbi jdbi) {
		return jdbi.onDemand(AccountRepository.class);
	}

	@Provides
	@Singleton
	public AccountReservationRepository provideAccountReservationRepository(Jdbi jdbi) {
		return jdbi.onDemand(AccountReservationRepository.class);
	}

	@Provides
	@Singleton
	public ProviderLinkRepository provideProviderLinkRepository(Jdbi jdbi) {
		return jdbi.onDemand(ProviderLinkRepository.class);
	}

	@Provides
	@Singleton
	public ProviderProfileRepository provideProviderProfileRepository(Jdbi jdbi) {
		return jdbi.onDemand(ProviderProfileRepository.class);
	}

	@Provides
	@Singleton
	public UsernameStateRepository provideUsernameStateRepository(Jdbi jdbi) {
		return jdbi.onDemand(UsernameStateRepository.class);
	}

	@Provides
	@Singleton
	public UsernameHistoryRepository provideUsernameHistoryRepository(Jdbi jdbi) {
		return jdbi.onDemand(UsernameHistoryRepository.class);
	}

}
