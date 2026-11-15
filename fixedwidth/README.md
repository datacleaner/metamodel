# metamodel-fixedwidth

Support for fixed-width text files (columns defined by character positions).

## Purpose

- `FixedWidthDataContext` and configuration classes for reading and writing files where each
  record occupies a fixed number of characters, with columns defined by line ranges.
- Builds on the CSV module for `Resource`-based file handling.

## Dependencies

- `metamodel-core` (compile)
- `metamodel-csv` (compile)

## Tests

JUnit 4. Run with:

```
mvn -pl fixedwidth test
```
