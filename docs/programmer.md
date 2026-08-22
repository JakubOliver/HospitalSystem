# Programmer documentation

## Detailed documentation

The detailed documentation of all classes, methods etc. can be generated via using javadoc by executing the following command: `mvn javadoc:aggregate`. After running this command you will find in this `docs/apidocs` directory static webpages with documentation. The main entry point is `docs/apidocs/index.html`.

Or you can visit web page [JavaDocs](https://kubinlabs.org/HospitalSystem/) where are JavaDocs generated.

## Test cases

You can run all tests cases with the command `mvn test`.

## Test coverage

Generate the combined project coverage report with `mvn clean verify`. The HTML
report is written to `application/target/site/jacoco-aggregate/index.html`.
