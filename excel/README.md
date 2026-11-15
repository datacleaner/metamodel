# metamodel-excel

Support for Excel spreadsheets (.xls and .xlsx).

## Purpose

- `ExcelDataContext` — read spreadsheets as tables; sheets become tables, rows become rows.
- Supports spreadsheet metadata such as column types inferred from cell content, formulas
  and named ranges.
- Uses Apache POI.

## Dependencies

- `metamodel-core` (compile)
- `org.apache.poi:poi-ooxml` (compile)
- `commons-logging` bridged to slf4j via `jcl-over-slf4j`

## Tests

JUnit 4 and EasyMock. Run with:

```
mvn -pl excel test
```
