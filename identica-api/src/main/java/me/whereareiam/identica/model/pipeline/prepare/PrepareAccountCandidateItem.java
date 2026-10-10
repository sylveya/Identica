package me.whereareiam.identica.model.pipeline.prepare;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.identica.model.identity.Account;
import me.whereareiam.identica.model.identity.provider.AccountProviderLink;
import me.whereareiam.identica.model.identity.provider.AccountProviderProfile;
import me.whereareiam.identica.pipeline.state.StateKey;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrepareAccountCandidateItem {
	/**
	 * Key of the account the connection is prepared for in a pipeline state. Only lives during one run and is never saved.
	 */
	public static final StateKey<PrepareAccountCandidateItem> KEY =
			StateKey.run("prepare:account-candidate", PrepareAccountCandidateItem.class);

	private @Nullable UUID uniqueId;
	private @Nullable String effectiveUsername;

	private @Nullable Account account;
	private @Nullable AccountProviderLink link;
	private @Nullable AccountProviderProfile profile;
}
