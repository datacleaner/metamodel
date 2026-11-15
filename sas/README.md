# metamodel-sas

Support for SAS datasets (.sas7bdat files).

## Purpose

- `SasDataContext` — read SAS table files as tables.
- Builds on the CSV module for `Resource`-based file handling.

## Dependencies

- `metamodel-core` (compile)
- `metamodel-csv` (compile)

## Tests

JUnit 4. Run with:

```
mvn -pl sas test
```
