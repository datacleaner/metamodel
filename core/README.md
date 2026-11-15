# metamodel-core

The core module contains the heart of MetaModel: the API for exploring and querying
datastores. All other modules depend on this one.

## Purpose

- `DataContext` and `UpdateableDataContext` — entry points for reading and writing
- `Schema`, `Table`, `Column`, `Relationship`, `ColumnType` — the schema model
- `Query` with `SelectItem`, `FilterItem`, `OrderByItem` and `GroupByItem`, plus a fluent
  query builder (`query.builder`) and a SQL-like string query parser (`query.parser`)
- DML/DDL builders: `create`, `insert`, `update`, `delete` and `drop` sub-packages
- `Resource` — abstraction over files, URLs, streams and byte arrays (`util`)
- Type conversion helpers (`convert`)
- Interceptor API for decorating data contexts (`intercept`)
- SPI for pluggable data contexts (`factory.DataContextFactory`, `factory.ResourceFactory`),
  registered via `META-INF/services`

## Dependencies

- `org.slf4j:slf4j-api` (compile)
- `com.google.guava:guava` (compile)

## Tests

JUnit 4 and EasyMock. Run with:

```
mvn -pl core test
```
