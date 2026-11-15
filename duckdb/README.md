# metamodel-duckdb

Support for DuckDB databases through its JDBC driver.

## Purpose

- `DuckDBDataContext` (via a `DataContextFactory`) — read and update DuckDB databases,
  in-memory or file-based.
- Builds on the JDBC module's `JdbcDataContext` with DuckDB-specific dialect handling.

## Dependencies

- `metamodel-jdbc` (compile)
- `org.duckdb:duckdb_jdbc` (compile)

## Tests

JUnit 4. Run with:

```
mvn -pl duckdb test
```
