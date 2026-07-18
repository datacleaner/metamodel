## Thank you!

Before anything else, thank you. Thank you for taking some of your precious time helping this project move forward.

This guide will help you get started with Apache MetaModel's development environment. You'll also find the set of rules you're expected to follow in order to submit improvements and fixes to Apache MetaModel.

### Build the code

Fork and clone the repository:

```
> git clone git@github.com:datacleaner/metamodel.git MetaModel
```

Try your first build:

```
> cd MetaModel
> mvn clean install
```

### Report issues and ideas

Please create and interact with GitHub issues here:
https://github.com/datacleaner/metamodel/issues

### Submitting your patch

When submitting your patch, keep in mind that it should be easy for others to review.

Please create and interact with GitHub Pull Requests here:
https://github.com/datacleaner/metamodel/pulls

### Coding guidelines

If you plan on submitting code, read this carefully. Please note it is not yet complete.

We stick to the [Google Java Style Guide](http://google-styleguide.googlecode.com/svn/trunk/javaguide.html), with a few modifications/exceptions:

* We often prefix instance variables with an underscore (_). This to easily distinguish between method local and instance variables, as well as avoiding the overuse of the 'this' keyword in e.g. setter methods.
* We format indentation using spaces, not tabs. We use 4 spaces for each indentation.
* We format line wrapping using a desired max line length of 120 characters.

### Releases and versioning

This project uses [Conventional Commits](https://www.conventionalcommits.org/) to determine version bumps and [semantic-release](https://semantic-release.gitbook.io/) to publish Maven artifacts to GitHub Packages.

When your Pull Request is merged to `main`, the release workflow analyzes the commits since the last release tag:

| Commit type | Version bump | Example |
|-------------|--------------|---------|
| `fix:` | Patch | `fix: correct null handling in query builder` |
| `feat:` | Minor | `feat: add support for ARRAY data types` |
| `feat!:` or `BREAKING CHANGE:` | Major | `feat!: drop deprecated API` |
| `chore:`, `docs:`, etc. | No release | `docs: update README` |

Release tags follow the existing `MetaModel-${version}` format (e.g. `MetaModel-5.3.7`). After a release, the `pom.xml` files are updated to the next `-SNAPSHOT` version automatically.

To publish from your local machine, configure `~/.m2/settings.xml` with a GitHub personal access token that has `read:packages` and `write:packages` scopes:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

Then run `mvn clean deploy`.
