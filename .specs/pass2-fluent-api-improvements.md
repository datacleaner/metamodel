# Spec: Pass 2 — Additive fluent-API improvements

## Goal
Add convenience methods to the public query/update/row API using Java 8 `default` methods or new overloads. Existing code must keep compiling unchanged.

## Scope
Public interfaces/classes in `core`, especially `DataSet`, `Row`, `RowBuilder`, `DataContext`. Implementations only where a `default` method is insufficient.

## Work items

### A. `DataSet.stream()` default method
Add to `DataSet`:

```java
default Stream<Row> stream() {
    return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED),
            false)
        .onClose(this::close);
}
```

Usage:

```java
try (Stream<Row> rows = dc.query()
        .from(table)
        .select(col)
        .execute()
        .stream()) {

    rows.map(r -> r.getValue(col, String.class))
        .filter(Objects::nonNull)
        .forEach(System.out::println);
}
```

**Why:** Users can compose directly with the Stream API, and the dataset is auto-closed when the try-block exits.

### B. `DataSet.findFirst()` / `findSingle()` default methods
Add to `DataSet`:

```java
default Optional<Row> findFirst() {
    try (DataSet ds = this) {
        return ds.next() ? Optional.of(ds.getRow()) : Optional.empty();
    }
}

default Optional<Row> findSingle() {
    try (DataSet ds = this) {
        if (!ds.next()) {
            return Optional.empty();
        }
        Row row = ds.getRow();
        if (ds.next()) {
            throw new MetaModelException("Expected exactly one row, but found more");
        }
        return Optional.of(row);
    }
}
```

Usage:

```java
String name = dc.query()
        .from(table)
        .select(nameCol)
        .where(idCol).eq(42)
        .execute()
        .findFirst()
        .map(r -> r.getValue(nameCol, String.class))
        .orElseThrow();
```

**Why:** Removes manual `next()/getRow()/close()` boilerplate and makes null handling explicit.

### C. Typed row value accessors
Add to `Row`:

```java
default <T> T getValue(SelectItem item, Class<T> type) {
    return type.cast(getValue(item));
}

default <T> T getValue(Column column, Class<T> type) {
    return type.cast(getValue(column));
}

default <T> T getValue(int index, Class<T> type) {
    return type.cast(getValue(index));
}

default <T> Optional<T> getValueOptional(SelectItem item, Class<T> type) {
    return Optional.ofNullable(getValue(item, type));
}

// ... analogous overloads for Column and index
```

Usage:

```java
String name = row.getValue(nameCol, String.class);
Integer age = row.getValue(ageCol, Integer.class);
```

**Why:** Eliminates manual casts and gives compile-time type safety.

### D. Bulk value setters for inserts/updates
Add to `RowBuilder`:

```java
default RB values(Map<String, Object> values) {
    values.forEach((name, value) -> value(name, value));
    return self();
}

default RB values(Row row) {
    for (SelectItem item : row.getSelectItems()) {
        value(item.getColumn(), row.getValue(item), row.getStyle(item));
    }
    return self();
}
```

`self()` will require either the existing `@SuppressWarnings("unchecked")` cast or a small protected helper. If `RowBuilder` cannot hold state, keep the methods `default` and cast `this` to `RB`.

Update `InsertInto`/`Update` syntax examples/tests to show:

```java
dc.executeUpdate(cb -> cb.insertInto(table)
        .values(Map.of("name", "Alice", "age", 30))
        .execute());
```

**Why:** Greatly reduces verbosity when populating from a DTO or copying a row.

### E. Shortcut `DataContext.query(Table)` overloads
Add to `DataContext`:

```java
default TableFromBuilder query(Table table) {
    return query().from(table);
}

default TableFromBuilder query(String tableName) {
    return query().from(tableName);
}

default TableFromBuilder query(Schema schema, String tableName) {
    return query().from(schema, tableName);
}

default TableFromBuilder query(String schemaName, String tableName) {
    return query().from(schemaName, tableName);
}
```

Usage:

```java
DataSet ds = dc.query(table)
        .selectAll()
        .where(idCol).eq(42)
        .execute();
```

**Why:** Removes the ever-present `.from(table)` step for single-table queries.

## Out of scope
- Breaking changes from initiative #3 (builder generics/sealed interfaces).
- Changing existing return types to `Optional`.
- Removing any deprecated methods.

## Acceptance criteria
1. `mvn clean test -pl core` passes.
2. `mvn clean install` passes for the whole reactor.
3. Add or extend tests in the relevant `SyntaxExamplesTest` classes:
   - `DataSet` stream/first/single tests.
   - `Row` typed getter tests.
   - Insert/update bulk `values(Map)` tests.
   - `DataContext.query(Table)` shortcut tests.
4. Verify backward compatibility: a small snippet compiled against the **old** API must still compile against the **new** API.
5. Update relevant `SyntaxExamplesTest` cases to demonstrate the new fluent syntax.
6. No method removals or signature changes on existing public/protected methods.
