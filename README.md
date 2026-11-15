# MetaModel (DataCleaner fork)

MetaModel is a Java library that provides a common interface for exploring and querying
different types of datastores: relational databases (JDBC), CSV files, Excel spreadsheets,
JSON, XML, fixed-width files, SAS files, ARFF files, POJOs and DuckDB. Rich querying
abilities are offered to datastores that do not otherwise support advanced querying, and a
unified view of the datastore structure is offered through a single model of schemas, tables,
columns and relationships.

<div>
<img src="https://metamodel.apache.org/img/logo.png" style="float: right; margin-left: 20px;" alt="MetaModel logo" />
</div>

## Repository layout

This is a Maven multi-module project. Each directory below is a Maven module with its own
`README.md` describing its purpose and dependencies:

| Directory | Artifact | Purpose |
|-----------|----------|---------|
| [`core/`](core/README.md) | `metamodel-core` | Base API: `DataContext`, `Schema`, `Query`, DML/DDL builders, `Resource` abstraction |
| [`pojo/`](pojo/README.md) | `metamodel-pojo` | In-memory data contexts backed by plain Java objects |
| [`fixedwidth/`](fixedwidth/README.md) | `metamodel-fixedwidth` | Fixed-width text files |
| [`excel/`](excel/README.md) | `metamodel-excel` | Excel spreadsheets (.xls, .xlsx) |
| [`csv/`](csv/README.md) | `metamodel-csv` | CSV files |
| [`arff/`](arff/README.md) | `metamodel-arff` | ARFF (Weka) files |
| [`sas/`](sas/README.md) | `metamodel-sas` | SAS (.sas7bdat) files |
| [`json/`](json/README.md) | `metamodel-json` | JSON files and documents |
| [`xml/`](xml/README.md) | `metamodel-xml` | XML files (SAX-based) |
| [`jdbc/`](jdbc/README.md) | `metamodel-jdbc` | JDBC datastores + SQL dialect rewriters |
| [`duckdb/`](duckdb/README.md) | `metamodel-duckdb` | DuckDB datastores |
| [`full/`](full/README.md) | `metamodel-full` | Convenience aggregate of all modules |

Dependency graph (simplified):

```
                 core
                 ^  ^  ^  ^
      pojo ──────┘  │  │  └────────────┐
      csv ──────────┘  │  └── jdbc ─── duckdb
      fixedwidth ← csv │
      arff ← csv       │
      sas ← csv        │
      excel ← core     │
      json ← core      │
      xml ← core       │
                 full (depends on all)
```

## Building the code

MetaModel uses Maven as its build tool and requires Java 21:

```
mvn clean install
```

## Running the integration tests

Most integration tests (especially in the `jdbc` module) are gated behind a configuration
file with connection properties and are skipped when that file is absent:

1. Copy `example-metamodel-integrationtest-configuration.properties` to your user home
   directory and remove the `example-` prefix, so it becomes
   `~/metamodel-integrationtest-configuration.properties`.
2. Modify the file to enable properties of the integration tests that you're interested in.
3. Re-run `mvn clean install`.

To spin up the datastores locally with Docker, see
[`docker-compose.yml`](docker-compose.yml):

```
docker compose up -d
```

A ready-made configuration file that matches this compose setup is available in
[`docker/metamodel-integrationtest-configuration.properties`](docker/metamodel-integrationtest-configuration.properties) —
copy it to your user home directory to run the PostgreSQL integration tests against the
Docker containers.

## Contributing

Please see [CONTRIBUTE.md](CONTRIBUTE.md).
