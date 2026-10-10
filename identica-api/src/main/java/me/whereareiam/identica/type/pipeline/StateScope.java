package me.whereareiam.identica.type.pipeline;

/**
 * Whether a value in a pipeline state outlives the run that holds it.
 */
public enum StateScope {
	/**
	 * Lives only during one run. The pipeline state store never saves it, so it never reaches another proxy or a
	 * later run.
	 */
	RUN,
	/**
	 * Saved with a pending pipeline, so a later run on any proxy continues with it.
	 */
	PERSISTED
}
