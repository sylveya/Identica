package me.whereareiam.identica.common.identity;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.whereareiam.identica.common.config.provider.UsernameConflictsProvider;
import me.whereareiam.identica.common.config.provider.UsernameMessagesProvider;
import me.whereareiam.identica.common.identity.account.DefaultAccountService;
import me.whereareiam.identica.common.identity.account.DefaultRegistrationAccountService;
import me.whereareiam.identica.common.identity.username.UsernameConflictContextFactory;
import me.whereareiam.identica.common.identity.username.UsernameConflictResolver;
import me.whereareiam.identica.common.identity.username.UsernameConflictType;
import me.whereareiam.identica.common.identity.username.UsernameLifecycle;
import me.whereareiam.identica.common.migration.DefaultMigrationService;
import me.whereareiam.identica.identity.IdentityService;
import me.whereareiam.identica.identity.account.AccountService;
import me.whereareiam.identica.identity.account.RegistrationAccountService;
import me.whereareiam.identica.identity.username.UsernameConflictHandler;
import me.whereareiam.identica.lifecycle.RuntimeLifecycle;
import me.whereareiam.identica.model.config.UsernameMessages;
import me.whereareiam.identica.service.MigrationService;

public class IdentityConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(AccountService.class).to(DefaultAccountService.class).asEagerSingleton();
		bind(RegistrationAccountService.class).to(DefaultRegistrationAccountService.class).asEagerSingleton();
		bind(MigrationService.class).to(DefaultMigrationService.class).asEagerSingleton();
		bind(IdentityService.class).to(DefaultIdentityService.class).asEagerSingleton();

		bind(UsernameConflictsProvider.class).asEagerSingleton();
		bind(UsernameMessagesProvider.class).asEagerSingleton();
		bind(UsernameMessages.class).toProvider(UsernameMessagesProvider.class);
		bind(UsernameConflictContextFactory.class).asEagerSingleton();
		bind(UsernameConflictResolver.class).asEagerSingleton();
		bind(UsernameConflictHandler.class).to(UsernameConflictResolver.class);
		bind(UsernameConflictType.class).asEagerSingleton();
		Multibinder.newSetBinder(binder(), RuntimeLifecycle.class).addBinding().to(UsernameLifecycle.class);
	}
}
