# metamodel-pojo

In-memory data contexts backed by plain Java objects (POJOs).

## Purpose

- `PojoDataContext` — a data context built from `PojoTable` definitions and rows supplied in
  code, useful for testing, prototyping and small datasets.
- No files or databases involved; data lives in the JVM heap.

## Dependencies

- `metamodel-core` (compile)

## Tests

JUnit 4. Run with:

```
mvn -pl pojo test
```
