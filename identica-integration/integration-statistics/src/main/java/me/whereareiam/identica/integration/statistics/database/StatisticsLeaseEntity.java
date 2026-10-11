package me.whereareiam.identica.integration.statistics.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.dialectica.EntitySchemaProvider;
import me.whereareiam.dialectica.annotation.Entity;
import me.whereareiam.statistica.adapter.jdbc.JdbcElection;

/**
 * The lease that elects the one proxy of a database that sends the account counts.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity(tableName = StatisticsLeaseEntity.TABLE)
public class StatisticsLeaseEntity implements EntitySchemaProvider {
	public static final String TABLE = "identica_statistics_lease";

	private int id;
	private String holder;
	private long expiresAt;

	@Override
	public String statement(String databaseType) {
		return JdbcElection.createTableStatement(TABLE);
	}
}
