package me.whereareiam.identica.pipeline.state.scenario.shared.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.identica.pipeline.state.StateKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdentityMetaItem {
	/**
	 * Key of the identity details of the run in a pipeline state. Saved with a pending pipeline.
	 */
	public static final StateKey<IdentityMetaItem> KEY = StateKey.persisted("identity:meta", IdentityMetaItem.class);

	private boolean resumed;
}
