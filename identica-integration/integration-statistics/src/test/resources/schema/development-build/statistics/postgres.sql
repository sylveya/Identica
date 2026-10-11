-- The statistics tables a development build of Identica 3.0.0 created on POSTGRES.
-- Generated from the DDL of commit 63f54991eaa175ca46cd76c3d64629c937653fb6 (origin/dev): JdbcElection.createTableStatement of Statistica 1.0.0.
-- Dialectica 1.0.2 ran each block below as one JDBC statement; run them the same way.
-- statement
CREATE TABLE IF NOT EXISTS identica_statistics_lease (id INT NOT NULL PRIMARY KEY, holder VARCHAR(36) NOT NULL, expires_at BIGINT NOT NULL)
