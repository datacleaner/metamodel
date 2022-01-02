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
