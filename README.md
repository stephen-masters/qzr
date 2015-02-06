Qzr
===

This is a simple web application, which asks a short series of questions to determine a person's maximum heart rate. There are a few different ways in which this can be calculated, so the aim is to demonstrate using a rules engine to ask a series of questions, which enable gradual refinement of results.

This is intended as a relatively minimal web application to demonstrate web services exposing Drools rules. However, it also acts as the author's playground for experimenting with a few different technologies, including:

  * Drools 6
  * Spring Boot
  * AngularJS

As such, it has ended somewhat more complex than might be expected for a tutorial in any one of these technologies. But hopefully not a lot. Attempts have been made to keep the different technologies separated into appropriate modules of the project. For instance, Spring makes minimal intrusion into the `qzr-rules` module. The web application Java code is limited to a service for the business logic and a REST API. The UI is provided by a single-page AngularJS application, which makes calls to the REST API.

For something similar, which tries harder to keep itself restricted to running Drools 6 on Spring Boot, take a look at: https://github.com/gratiartis/buspass-ws

**Run the application**

I have set this up with a Maven build and it also requires Java 8. So `cd` into the project directory and run:

    mvn package && java -jar target/qzr-web-*-SNAPSHOT.jar --spring.profiles.active=scratch,drools

For convenience, there's a script called `run.sh` in the root of the project, which fires off the Java portion of that command line.

Once it's running, you can point a browser at http://127.0.0.1:40080/ to see the site working.

