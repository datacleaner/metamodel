# Spec: Pass 1 — Non-breaking Java syntax modernisation

## Goal
Adopt Java 8–21 idioms **inside** the existing implementation and tests without changing any public method signatures. The public API must remain byte-code compatible.

## Scope
Primarily the `core` module; other modules only if they contain the same outdated patterns.

## Work items

### 1. Pattern-matching `instanceof`
Replace old cast-after-instanceof patterns.

```java
// before
if (dataSet instanceof AbstractDataSet) {
    _header = ((AbstractDataSet) dataSet).getHeader();
}

// after
if (dataSet instanceof AbstractDataSet ads) {
    _header = ads.getHeader();
}
```

Candidate files (found via `rg "instanceof .*\\)"`):
`AbstractDataSet`, `DefaultCompiledQuery`, `MetaModelHelper`, `SelectItem`, `ObjectComparator`, `TimeComparator`, `NumberComparator`, `BooleanComparator`, `CollectionUtils`, `AbstractTable`, `AbstractSchema`, `MutableRelationship`, `FileHelper`, `BaseObject`, etc.

### 2. Use `var` for local variables
Apply `var` only where the type is obvious from the right-hand side. Do **not** change public field or method signatures.

```java
// before
SatisfiedSelectBuilder<?> builder = dc.query().from(table).select(col1, col2);

// after
var builder = dc.query().from(table).select(col1, col2);
```

Focus on tests, examples, and internal implementation locals.

### 3. Collection factory methods
Audit `new ArrayList<>()`, `new HashSet<>()`, `Collections.emptyList()`, `Arrays.asList(...)` usage. Replace with `List.of(...)`, `Set.of(...)`, `Map.of(...)` where the returned collection is intentionally immutable.

```java
// before
return Collections.emptyList();

// after
return List.of();
```

Be conservative: if the calling code mutates the collection, keep a mutable structure.

### 4. Add `@SafeVarargs`
Apply to `final`/`static` varargs helper methods that currently need unchecked-varargs warnings suppressed.

### 5. Switch expressions for enum dispatch
Replace verbose `switch` statements with arrow/enum switch expressions where there is a single return value.

Candidates: `OperatorType`, `ColumnType`, `TableType`, `Direction`.

```java
// before
switch (dir) {
    case ASC: return "ASC";
    case DESC: return "DESC";
    default: throw ...
}

// after
return switch (dir) {
    case ASC -> "ASC";
    case DESC -> "DESC";
};
```

### 6. Replace anonymous classes with lambdas/method references
Search for `new UpdateScript(...)`, `new Runnable(...)`, `new Comparator(...)` etc. Replace with lambdas or method references where the interface is functional.

```java
// before
dc.executeUpdate(new UpdateScript() {
    public void run(UpdateCallback cb) {
        cb.insertInto(table).value(col, "foo").execute();
    }
});

// after (already supported today - this is just cleanup)
dc.executeUpdate(cb -> cb.insertInto(table).value(col, "foo").execute());
```

### 7. `Objects.requireNonNull`, `String.isBlank`, `String.lines`
Backfill remaining manual null checks and string helpers where they make the code shorter.

### 8. `try`-with-resources
Where a class already implements `Closeable` (e.g. `DataSet`), consider converting manual `try/finally` close logic into `try`-with-resources. Only do this when the behaviour stays identical.

## Out of scope
- No removal of existing public methods.
- No addition of new public methods.
- No generic signature changes.
- No records, sealed types, or `Optional` return-type changes.

## Acceptance criteria
1. `mvn clean test -pl core` passes.
2. `mvn clean install` (with appropriate skips) passes for the whole reactor.
3. No new compiler warnings introduced; ideally the warning count drops.
4. Public API is unchanged: verify by diffing the public/protected signatures, or by running `japicmp` against the previous JAR if you want to be strict.
5. No behavioural changes — tests should only be updated for syntax, not expectations.
