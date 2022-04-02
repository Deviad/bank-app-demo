# Bank demo application

# Goal
Create an application that can create a user and a bank account associated with it. 
Upon account creation, the application should send a message from the "account" module to the "transaction" module. 
After the transaction is registered in the DB the *account balance* kept in the "account" module should be updated.

# Quality constraints

For this demo I have decided to put the accent on reliability. 
Hence, I have shown how it's possible to retry multiple times an operation, and then, also, retry that at a later time
after that its state has been saved in the database and its status has been marked as RETRY.

# Technical decisions

## In memory event bus and Kafka
The account module portrays the implementation of a state machine where events are passed through an event bus. 
Events are stored in a database (H2) and in case of failure, the failed action will be retried. 
For the transaction part I have used Kafka instead to show how roughly the same thing can be done using Kafka. 
Kafka, if configured properly, allows to have in place "Exactly once per request processing" and it also allows to persist 
all the events as though it was a database. The former allows us to reduce the need of creating/devising distributed locking mechanisms.

In some cases these two approaches could be both viable and there are a few factors that come into play when picking one, 
for example:
- the possibility to have a Kafka instance in a reasonable time (especially when this implies the intervention of an external team);
- the sustainability of the cost of the Kafka instance for the specific project;
- the possibility to use in the specific project some Kafka binders which allow making life easier instead of configuring
everything from scratch.

For this specific app, the event bus has been used as a means of communication for domain events and to put in communication 
the application layer with the domain layer. However, the same system can be used between different artifacts (applications)
for them to perform some actions.

## H2
The application uses H2 as an inmemory database to reduce the dependency on external tools.


# User manual

To run the application you need Java 11 and Docker.
There are essentially two possibilities.

- `docker-compose up` will run all the application components inside docker containers.
- For dev purposes, instead, you may want to run only Kafka inside a container and start up the account and 
the transaction component in your IDE. This is obtained with `docker-compose -f Docker-compose-local.yml up`
When you are done, it is always a good thing to use docker-compose down to remove the leftovers.

Tests can be run with `./gradlew clean test`, also these make use of docker. 

Once the application is running you will have different tools at your disposal

| MODULE NAME | SWAGGER                             | H2 console |
|-------------|-------------------------------------|------------|
| Account     |    http://localhost:8080/swagger-ui | http://localhost:8082 |
| Transaction |    N/A                              | http://localhost:8083  |

From Swagger you can send a payload to the user endpoint (POST /user)<br>
To verify that the account was created and the balance, if any, was added you can use GET /user/username/{username}



# Next Steps

Configure Kafka so that it supports "Exactly once per request processing" and persisting all events.

https://www.confluent.io/blog/enabling-exactly-once-kafka-streams/ <br>

https://www.confluent.io/blog/okay-store-data-apache-kafka/
