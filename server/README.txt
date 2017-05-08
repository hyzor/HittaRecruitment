Hej!

The goal with the assignment is to show how you take on implementing a simple
Java-projekt. You choose how much time and how full-blown you want the solution to be.
It's good to takes notes on how you reasoned.

Good luck!


=== Time and boundaries

It is important that you implement a subset of the functionality complete
rather than implementing parts sporadically or take on everything and
think the task is too time consuming. Then choose rather to implement a
subset, for example, CREATE or DOWNLOAD.



=== Kriterier

Vi kommer främst att bedömma följande aspekter av din lösning: 
1. Korrekthet enligt verifikationstestet samt enligt gängse HTTP-normer
2. Enkelhet och testbarhet
3. Robusthet (t.ex. felhantering, trådsäkerhet)
4. Kunskap om Java standard-API:er och eventuella tredjepartsbibliotek



=== Assignment

Write an HTTP server that stores and provides information
about people. The data does not need to be stored persistently.
XML is used for all input.
JSON used for all output.



=== Specifikation

1. Create person:
    POST http://127.0.0.1:8080/person
    * In the response, the address of the created person should be set as the value for the location header.
    * It is up to you how repeated calls to CREATE are handled, either create new persons or return an existing one

2. Update person:
    PUT http://127.0.0.1:8080/person/<id>
    * New data should overwrite old data

3. Get given person:
    GET  http://127.0.0.1:8080/person/<id>
    * Response with a JSON that corresponds to the format in the [verification]-module
   
4: List all persons or only those given by a gender parameter:
    GET  http://127.0.0.1:8080/person?[gender=<male|female>]
    * Response with a JSON with all persons
    * If the parameter [gender] is sent then only show persons of that gender



=== Installation and functional test:

1. Install Maven 3: http://maven.apache.org/   
2. Run: [mvn jetty:run] in the directory [server], this will start a web-server with the port 8080
3. Browse to http://localhost:8080/, you should see a static example of a JSON for a person
4. If you use Eclipse you can run [mvn eclipse:eclipse]
   in the sub-directories (server, verification) to create project paths
5. If you version-control your solution use git, its already initiaded
6. If you write tests, write them in the [server]-module. Changes made in the [verification]-module will be lost



=== Verification of data

The JSON-data that is produced from the service does not need to be whitespace-formatted
like the exemples. Structure and keys must match.



=== Exemple data

You can find examples on in- and out-data in the directory: verification/src/main/resources



=== Test

You can test by your self by running:
1. from server-projektet, run: [mvn jetty:run]
2. from verification-projektet run: [mvn test]
