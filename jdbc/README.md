# metamodel-jdbc

Support for relational databases through JDBC, plus SQL dialect handling.

## Purpose

- `JdbcDataContext` — read and update relational databases, with automatic schema model
  inspection via JDBC metadata.
- SQL dialect rewriters (`JdbcDialect` implementations) that adapt generated SQL and
  queries to specific databases: MySQL, PostgreSQL, Oracle, SQL Server, DB2, Firebird,
  Hive, H2, HSQLDB, SQLite, Derby and more.
- Connection handling via `DataSource` or `Connection`, with pooled `BasicDataSource`
  support (commons-dbcp2).
- Registers a `DataContextFactory` in `META-INF/services`.

## Dependencies

- `metamodel-core` (compile)
- `com.fasterxml.jackson.core` (compile, used by dialects)
- JDBC drivers are provided by the user; PostgreSQL, MySQL, MSSQL, H2, HSQLDB, Derby and
  SQLite drivers are bundled for development and testing.

## Integration tests

The `integrationtests` package contains tests for PostgreSQL, MySQL, Oracle, SQL Server,
DB2 and Firebird. They are gated behind
`~/metamodel-integrationtest-configuration.properties` — tests silently skip when the file
is missing or the properties for a database type are not enabled. See the root README and
`docker-compose.yml` for a ready-made Docker setup.

## Tests

JUnit 4 and EasyMock. Run with:

```
mvn -pl jdbc -am test
```
