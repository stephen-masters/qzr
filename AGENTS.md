# AGENTS.md

Guidance for anyone working in this repository with a coding agent (Claude
Code, Codex, Cursor, Gemini CLI or whatever else reads this file) and a fair
summary of the house rules for contributors who use none.

## What this repository is

**Qzr** is a small demonstration of Drools rules exposed through a Spring
Boot web application. It is a teaching example, not a library or a product:
nothing is released or deployed.

The point is this. A Drools session can be long-lived and build up
knowledge gradually. Each visitor's HTTP session holds its own `KieSession`
(`HrMaxQuizService` is session-scoped). The rules in `hrmax.drl` decide which
question to ask from what is already known, turn answers into `Known` facts
with logical insertion, and calculate a maximum heart rate that is refined as
more is learnt. Because inferences are logically inserted, retracting an
answer retracts everything that followed from it. A listener streams each
rule activation to the page over STOMP, so a reader can watch the engine
reason.

## Hard rules

### The rules are the demo

`hrmax.drl` and `kmodule.xml` change only when the behaviour of the quiz is
meant to change, or when a new Drools release requires it. A dependency
upgrade that needs a rule rewritten should say so in its commit message.
`HrmaxRulesTest` pins what the rules do; do not weaken its assertions to make
a change pass.

### Keep the API and the page in step

The page (`templates/index.html`, `static/js/app.js`) calls the endpoints
under `/api/quizzes/health` and subscribes to `/queue/agendaevents/` on the
`/drools` STOMP endpoint. `HrMaxQuizControllerTest` covers each endpoint and
`AgendaEventPublishingTest` the WebSocket. A change to a path or a JSON field
is made in the controller, the page, those tests and the Postman collection
in `qzr-web/src/test/postman` in the same commit.

### No front-end framework or build step

The page is plain JavaScript and CSS served from `src/main/resources/static`,
with no framework, no bundler and no scripts from a CDN. It speaks STOMP
directly over a WebSocket rather than loading a client library.

### Stay on current LTS versions

The project tracks the latest LTS JDK (`java.version` in `pom.xml`, and
`java-version` in `.github/workflows/ci.yml` and `security.yml`; change them
together) and the latest Spring Boot GA release. Dependency versions come
from the Spring Boot parent's dependency management: do not pin a version
that the parent already manages. The root `pom.xml` pins only what it does
not manage (Drools, Joda-Time, Commons BeanUtils).

## Layout

| Path | What |
|---|---|
| `pom.xml` | Parent POM: Spring Boot parent, the enforcer, Spotless and the compiler settings for both modules |
| `sctrcd-drools/` | Drools utilities: `DroolsUtil`, `FactFinder`, `BeanMatcher`, and logging and tracking event listeners |
| `qzr-web/src/main/resources/com/sctrcd/qzr/hrmax/hrmax.drl` | The rules |
| `qzr-web/src/main/resources/META-INF/kmodule.xml` | The `HrmaxKbase` knowledge base and `HrmaxSession` session |
| `qzr-web/src/main/java/.../services/HrMaxQuizService.java` | The session-scoped service around one `KieSession` |
| `qzr-web/src/main/java/.../web/controllers/HrMaxQuizController.java` | The REST API |
| `qzr-web/src/main/java/.../WebSocketConfig.java` | The STOMP broker and the `/drools` endpoint |
| `qzr-web/src/main/resources/templates/index.html`, `static/` | The page |
| `.githooks/commit-msg` | Conventional Commits check, shared from `scattercode/release-pipelines` |

## Checks

```bash
./mvnw spotless:apply   # format the Java sources and the POMs
./mvnw verify           # everything below, and the tests
```

`./mvnw verify` runs, in order:

- the **enforcer**: the JDK is at least `java.version`, Maven is at least
  3.9, and dependency versions converge;
- the **Spotless** check (palantir-java-format for Java, sortpom for the
  POMs) in the `validate` phase;
- the **compiler** with `-Xlint:all -Werror`, so any warning fails the build;
- the **tests**: the rules, the REST API through MockMvc, the page, and the
  WebSocket on a running server.

It needs JDK 25; the Maven wrapper fetches Maven itself. All of it must
pass before a commit. The lefthook hooks run the format check before each
commit and the full build before each push; CI runs the build and a Trivy
scan of the `qzr-web` SBOM on every push and pull request.

## Conventions

- British English; sentence case for headings.
- Conventional Commits, enforced by `.githooks/commit-msg` and, for pull
  request titles, by `pr-title.yml`. Dependency updates are `build:`,
  workflow changes `ci:`.
- `.githooks/commit-msg` is a byte-for-byte copy of the template in
  `scattercode/release-pipelines`; CI checks it has not drifted. Change it
  there, not here.
- `main` is protected: changes arrive through pull requests with the CI
  checks passing.
- Hook setup in a fresh clone: `lefthook install`, or without lefthook,
  `git config core.hooksPath .githooks` for the commit message check alone.
