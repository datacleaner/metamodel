# Agent Notes: MetaModel

## Project Overview
MetaModel is a Java library that provides a unified querying and schema model over different datastores (JDBC, CSV, Excel, JSON, XML, POJO, ARFF, fixed-width). It lives at `github.com:datacleaner/metamodel` under branch `main`.

## Build System
- **Build tool:** Maven
- **GroupId:** `org.datacleaner.metamodel`
- **ArtifactId:** `MetaModel`
- **Version:** `5.3.7-SNAPSHOT`
- **Java level:** 21 (`source`/`target` in root POM)

## Module Layout
Standard Maven multi-module project. Modules in root POM:
- `core` – Base API (`DataContext`, `DataSet`, `Schema`, `Query`, `Table`, `Column`, etc.)
- `pojo` – POJO-based data contexts
- `fixedwidth` – Fixed-width file support
- `excel` – Excel (.xls/.xlsx) support
- `csv` – CSV file support
- `arff` – ARFF (Weka) file support
- `json` – JSON support
- `xml` – XML (SAX/DOM) support
- `jdbc` – JDBC datastore support + SQL dialect rewriters
- `full` – Aggregates all modules for convenient single-dependency usage

## Package Structure
- **Root package:** `org.datacleaner.metamodel`
  - This was previously `org.apache.metamodel` (in source) and `org.eobjects.metamodel` (in Maven).
  - **All code should use `org.datacleaner.metamodel`.**
- Sub-packages:
  - `data` – `DataSet`, `Row`, `Document`, etc.
  - `schema` – `Schema`, `Table`, `Column`, `ColumnType`, etc.
  - `query` – `Query`, `SelectItem`, `FilterItem`, `AggregateFunction`, etc.
  - `query.builder` – Fluent query builder API
  - `query.parser` – SQL-like string parsing
  - `factory` – `DataContextFactory`, `ResourceFactory`, registry SPI
  - `create` / `insert` / `update` / `delete` / `drop` – DDL/DML builders
  - `intercept` – Interceptor API for decorating data contexts
  - `convert` – Type-conversion logic
  - `util` – `Resource`, `FileHelper`, `SimpleTableDef`, etc.

## SPI / Service Loading
Many modules register factories via `META-INF/services`:
- `org.datacleaner.metamodel.factory.DataContextFactory`
- `org.datacleaner.metamodel.factory.ResourceFactory`
If you add a new `DataContextFactory`, register it under the correct service file name.

## Key Conventions
1. **Minimum changes policy:** The project owner prefers focused PRs. Avoid refactoring logic when doing mechanical changes.
2. **No automatic git push:** All `git push` operations must be explicitly requested by the user. Never push commits automatically.
2. **Tests:** Uses JUnit 4 and EasyMock. Many JDBC integration tests exist but are gated behind configuration properties (not run by default).
3. **License headers:** Apache License 2.0. `apache-rat-plugin` enforces headers; if you create new files, include the standard header.
4. **Compiler args:** `--add-opens=java.base/java.lang=ALL_UNNAMED --add-opens=java.base/java.io=ALL_UNNAMED` passed to Surefire for reflection-heavy tests on Java 21.

## Common Agent Tasks
- **Adding a module:** Follow existing module pattern (see `csv`, `json`, etc.): create `pom.xml` with parent `org.datacleaner.metamodel:MetaModel`, add `<module>` to root POM, add to `full` module dependencies, register `DataContextFactory` in `META-INF/services`.
- **Renaming packages/groupIds:** Must touch all of: Java source, `pom.xml` references, `META-INF/services` file names **and** contents, and directory tree.
- **Running tests:** `mvn test` runs unit tests. Integration tests (e.g., `jdbc/src/test/java/.../integrationtests/`) usually require `example-metamodel-integrationtest-configuration.properties` or environment-specific DB setup.
