# metamodel-csv

Support for CSV (comma-separated values) files.

## Purpose

- `CsvDataContext` — read CSV files as tables, with configurable separators, quotes,
  escape characters and headers.
- `CsvConfiguration` and `CsvWriter` for writing.
- Provides the `Resource`-based file handling that `fixedwidth`, `arff` and `sas` also build on.

## Dependencies

- `metamodel-core` (compile)
- `com.opencsv:opencsv` (compile)

## Tests

JUnit 4. Run with:

```
mvn -pl csv test
```
