Qzr
===

This is a simple web application, which asks a short series of questions to determine a person's
maximum heart rate. There are a few different ways in which this can be calculated, so the aim is
to demonstrate using a rules engine to ask a series of questions, which enable gradual refinement
of results.

It is a relatively small web application demonstrating web services that expose Drools rules, and
it brings together:

* [Apache KIE Drools](https://www.drools.org/) 10, with the rules in
  [`hrmax.drl`](qzr-web/src/main/resources/com/sctrcd/qzr/hrmax/hrmax.drl) and a knowledge base
  declared in [`kmodule.xml`](qzr-web/src/main/resources/META-INF/kmodule.xml)
* Spring Boot 4, with a REST API using Spring HATEOAS links
* STOMP over a WebSocket, which streams the name of each rule as it fires to the page
* a single page in plain JavaScript, with no framework and no build step

| Module | What |
|---|---|
| `sctrcd-drools` | Utilities for working with Drools: finding facts in a session, matching them on bean properties, and listeners that log or track rule activations |
| `qzr-web` | The rules, a session-scoped service holding one Drools session per visitor, the REST API at `/api/quizzes/health`, and the page |

Each visitor's HTTP session gets its own long-running Drools session. The rules ask for a date of
birth, then a gender, and calculate a maximum heart rate from whatever is known, refining it as
answers arrive. Retracting an answer retracts everything inferred from it.

For something similar, which tries harder to keep itself restricted to running Drools on Spring
Boot, take a look at <https://github.com/gratiartis/buspass-ws>.

Running it
----------

It needs Java 25. The Maven wrapper downloads Maven itself, so to run a full build, including the
format check and the tests:

    ./mvnw clean verify

Then start the application:

    java -jar qzr-web/target/qzr-web-1.0.0-SNAPSHOT.jar

Once it's running, point a browser at <http://127.0.0.1:40080/> to see the site working. The
Postman collection in `qzr-web/src/test/postman` exercises the API directly.

If the format check fails, `./mvnw spotless:apply` fixes it. To install the git hooks, which check
formatting before each commit and run the build before each push:

    lefthook install

See [AGENTS.md](AGENTS.md) for the conventions and [SECURITY.md](SECURITY.md) for reporting a
vulnerability.

Licensed under the [Apache License, Version 2.0](LICENSE).
