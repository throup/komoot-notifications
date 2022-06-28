# komoot-notifications

This project contains a lightweight HTTP server, which provides one public endpoint: `/hook`.

This endpoint expects to receive HTTP notifications from an AWS SNS topic (manually configured).

When a notification is received, it will be processed (according to details in [challenge.md](challenge.md)) and post a result payload to a configurable HTTP endpoint.

## Technologies
This project is built with Scala 3, using the following tooling:

- Cats and Cats Effect
- Http4S server and client
- Circe JSON parsing
- ScalaTest and ScalaCheck

## Build and compile
There is a `build.sbt` provided for the SBT build tool (it is assumed SBT is already installed).

The test suite can be run simply using:
```
$ sbt test
```

A docker image (`komoot:latest`) can be built, and published to the local repository, like so:
```
$ sbt docker:publishLocal
```

or if SBT is configured to publish to a non-local repository (not documented here):
```
$ sbt docker:publish
```

The image can be published directly to an AWS ECR repository like so:
```
$ export AWS_SECRET_KEY="<obtain key from AWS console>"
$ export AWS_SECRET_KEY_ID="<obtain key id from AWS console>"

$ sbt ecr:push
```

## Running the service
There are configurable properties for the service. These have idiot-proof defaults (just in case!) and can be overridden using environment variables:

|Variable                       |Purpose                                                                                       |Default                        |
|-------------------------------|----------------------------------------------------------------------------------------------|-------------------------------|
|`SENDER_EMAIL`                 |Email address, to be included in the resulting payload, indicating the sender of the messages.|email@example.com              |
|`URL_POST_WELCOME_NOTIFICATION`|URL of the endpoint to which the result payload will be POSTed.                               |http://localhost:8080/mock/push|

_note: the default endpoint is implemented within this project as a mock endpoint which returns an empty success response._

The main server is implemented as an `IOApp`, and will launch an HTTP server at http://localhost:8080.

It can be started locally with:
```
$ sbt run
```

Alternatively, a docker container can be deployed (see build instructions above). In most circumstances, you should bind port 8080 in order to reach the server.

## Subscribe to SNS topic
A valid subscription to an SNS topic should be created manually.

The service exposes an endpoint: `http://<domain-name>:8080/hook` which can be used for the subscription.

The topic payload is expected to match that described in [challenge.md](challenge.md).


## Devlopment notes
### Data storage
As this was built as a small prototype service, expected to run for a couple of hours, as a single instance, in the cloud, it uses an in-memory datastore. This has been implemented using a `TrieMap` to provide thread-safety. If this prototype was ever adopted into a real service, I would expect to reimplement the `UserRepository` trait with something backed by an external, shared datastore like Redis, PostgreSQL or DynamoDB.

### Test coverage
In order to move fast with confidence for the prototype, the domain logic has been covered by unit and property-based tests, whereas the infrastructure has been tested in action.

### Deployment
The service was deployed to AWS, running as a container in ECS, on Monday 27 June 2022.
The approximate running hours were 14:30 -- 17:30 CEST.