# CMC document management client

This is the backend service for Civil Money Claims.
The service provides a set methods to integrate with document management.
The two main responsibilities are:
 - upload document/s to document management,
 - download document/s from document management

## Getting started

### Prerequisites

- [JDK 8](https://www.oracle.com/java)
- [Docker](https://www.docker.com)

## Usage

Just include the library as your dependency and you will be to use the client class. Health check for DM service is provided as well.

Components provided by this library will get automatically configured in a Spring context if `document_management.url` configuration property is defined and does not equal `false`. 
 
### Building

The project uses [Gradle](https://gradle.org) as a build tool but you don't have install it locally since there is a
`./gradlew` wrapper script.  

To build project please execute the following command:

```bash
    ./gradlew build
```

## Developing

### Unit tests

To run all unit tests please execute the following command:

```bash
    ./gradlew test
```

### Coding style tests

To run all checks (including unit tests) please execute the following command:

```bash
    ./gradlew check
```

## Versioning

We use [SemVer](http://semver.org/) for versioning.
For the versions available, see the tags on this repository.

## License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details.
