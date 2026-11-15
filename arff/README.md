# metamodel-arff

Support for ARFF (Attribute-Relation File Format) files used by Weka.

## Purpose

- `ArffDataContext` — read `.arff` files as tables, interpreting the `@attribute`/`@data`
  declarations of the ARFF format.
- Builds on the CSV module for the actual data parsing.

## Dependencies

- `metamodel-core` (compile)
- `metamodel-csv` (compile)
- `com.google.guava:guava` (compile)

## Tests

JUnit 4 and EasyMock. Run with:

```
mvn -pl arff test
```
