package me.whereareiam.identica.integration.statistics.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.dialectica.entity.annotation.Id;
import me.whereareiam.dialectica.entity.annotation.Length;
import me.whereareiam.dialectica.entity.annotation.Table;

/**
 * The lease that elects the one proxy of a database that sends the account counts. Statistica's
 * {@code JdbcElection} reads and writes it.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(StatisticsLeaseEntity.TABLE)
public class StatisticsLeaseEntity {
	public static final String TABLE = "identica_statistics_lease";

	@Id
	private int id;
	@Length(36)
	private String holder;
	private long expiresAt;
}
