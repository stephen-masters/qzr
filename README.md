Qzr
===

This is a simple web application, which asks a short series of questions to determine a person's maximum heart rate. There are a few different ways in which this can be calculated, so the aim is to demonstrate using a rules engine to ask a series of questions, which enable gradual refinement of results.

This is intended as a relatively minimal web application to demonstrate web services exposing Drools rules. However, it also acts as the author's playground for experimenting with a few different technologies, including:

  Drools 6
  Spring Boot
  AngularJS

As such, it has ended somewhat more complex than might be expected for a tutorial in any one of these technologies. But hopefully not a lot. Attempts have been made to keep the different technologies separated into appropriate modules of the project. For instance, Spring makes minimal intrusion into the qzr-rules module. The web application Java code is limited to a service for the business logic and a REST API. The UI is provided by a single-page Angular JS application, which makes calls to the REST API.
