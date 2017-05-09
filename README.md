# HittaRecruitment

### About
This is a recruitment project for Hitta.se. A RESTful service is implemented that adds and modifies persons on a local server.

### Implementation details
rdf4j (http://rdf4j.org/) is used for RDF XML parsing. FOAF Person resources are extracted from XML files using the RDF model.

Persons are stored as regular Java objects, and are converted to JsonObjects during runtime, when needed using the javax.json library in javax.json-api and org.glassfish.

Each person has a generated ID (incremental number) that is atomic and thread safe. The persons are kept in a concurrent hash map, where keys are a generated hash of each person. The hash is calculated from the given name, family name, gender, email and homepage strings.

#### Library versions
* Jetty: 9.4.5.v20170502
* rdf4j: 2.2.1
* javax servlet: 3.1.0
* JSON: (API: 1.0. Glassfish JSON: 1.0.4)

#### Environments
* IDE: Eclipse (Neon.3 Release (4.6.3))
* OS: Ubuntu MATE 16.04.2 LTS 64-bit
* REST client: Postman for Chrome (4.10.7)

#### Classes
* PersonServer: Sets up a new server on 8080 and handles the servlets (only one used for now, used for the /person/* context path).
* PersonServlet: The servlet that handles most of the functionality. GET, PUT and POST are handled here.
* Person: Java class that encapsulates person information. Also has some helper functions such as JsonObject conversion.

### How to run
I personally used Eclipse for development. Compile the classes and then run PersonServer. Make sure to include the other classes in the classpath. Then connect to http://localhost:8080 and use the service as specified in the README.txt file.
