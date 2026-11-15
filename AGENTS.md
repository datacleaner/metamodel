# AGENTS.md — MetaModel

Guidance for AI coding agents working in this repository.

## Start here

- [README.md](README.md) — project overview, module map, build and integration test setup
- [CONTRIBUTE.md](CONTRIBUTE.md) — contribution workflow, coding style, release/versioning

## Key facts

- Maven multi-module project, Java 21, group id `org.datacleaner.metamodel`.
- Every module directory has a `README.md` (purpose, dependencies) and an `AGENTS.md`
  that delegates to it. Read the relevant module README before touching its code.
- All code lives under the Java package `org.datacleaner.metamodel` (previously
  `org.apache.metamodel` / `org.eobjects.metamodel`).
- Tests use JUnit 4 and EasyMock. JDBC integration tests are gated behind connection
  properties in `~/metamodel-integrationtest-configuration.properties`; without that file
  they silently skip. See the root README for a Docker Compose setup that provides
  test datastores.
- New files need the Apache License 2.0 header (`apache-rat-plugin` enforces it).
- Formatting: Google Java Style with 4-space indentation, 120-char lines, instance
  variables prefixed with underscore (`_`).
- Releases use Conventional Commits (`feat:`, `fix:`, `feat!:`) with semantic-release;
  see CONTRIBUTE.md.
- Never `git push` unless the user explicitly asks.

## Common commands

```
mvn clean install                  # full build + unit tests
mvn -pl core test                  # unit tests for one module
mvn -pl jdbc -am test              # jdbc tests (integration tests need the config file)
docker compose up -d               # datastores for integration tests
```

## Adding a module

Follow the pattern of existing modules (e.g. `csv/`, `json/`):

1. Create the module `pom.xml` with parent `org.datacleaner.metamodel:metamodel`.
2. Register it in the root `pom.xml` `<modules>` and add it to the `full` module.
3. Implement `DataContextFactory` and register it via
   `META-INF/services/org.datacleaner.metamodel.factory.DataContextFactory`.
4. Add a module `README.md` and `AGENTS.md`.
