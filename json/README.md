# metamodel-json

Support for JSON files and JSON documents.

## Purpose

- `JsonDataContext` — read JSON documents as tables; JSON arrays become rows.
- Supports deducing schemas from sample documents and incremental parsing for large files.
- Uses Jackson.

## Dependencies

- `metamodel-core` (compile)
- `com.fasterxml.jackson.core:jackson-core` and `jackson-databind` (compile)

## Tests

JUnit 4. Run with:

```
mvn -pl json test
```
