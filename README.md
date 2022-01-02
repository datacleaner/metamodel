## Apache MetaModel (DataCleaner fork)

MetaModel is a data access framework, providing a common interface for exploration and querying of different types of datastores.

<div>
<img src="http://metamodel.apache.org/img/logo.png" style="float: right; margin-left: 20px;" alt="MetaModel logo" />
</div>

### Building the code

MetaModel uses maven as it's build tool. Code can be built with:

```
mvn clean install
```

### Running the integration tests

 1. Copy the file 'example-metamodel-integrationtest-configuration.properties' to your user home.
 2. Remove the 'example-' prefix from its filename
 3. Modify the file to enable properties of the integration tests that you're interested in.
 4. Re-run "mvn clean install".

### Contributing

Please see [CONTRIBUTE.md](CONTRIBUTE.md)
