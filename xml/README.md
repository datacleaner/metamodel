# metamodel-xml

Support for XML files.

## Purpose

- `XmlDataContext` — read XML documents as tables; mapping is configurable via
  `XmlTableSpec`/`XmlColumnSpec` (XPath-like expressions for rows and columns).
- SAX-based streaming parser for large documents.

## Dependencies

- `metamodel-core` (compile)

## Tests

JUnit 4. Run with:

```
mvn -pl xml test
```
