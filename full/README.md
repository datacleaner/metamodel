# metamodel-full

Convenience aggregate module that pulls in all other MetaModel modules as a single
dependency. Contains no code of its own.

## Purpose

- Depend on `metamodel-full` to get every datastore module (core, pojo, fixedwidth, excel,
  csv, arff, sas, json, xml, jdbc, duckdb) at once.
- Also produces a packaged assembly for releases.

## Dependencies

- All other `metamodel-*` modules (compile)

## Tests

JUnit 4. Run with:

```
mvn -pl full test
```
