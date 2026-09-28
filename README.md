# Document management client

![](https://github.com/hmcts/document-management-client/workflows/CI/badge.svg)
[![Download](https://jitpack.io/v/hmcts/ccd-client.svg) ](https://jitpack.io/#hmcts/ccd-client)

The service provides a set methods to integrate with document management.
The two main responsibilities are:
 - upload document/s to document management,
 - download document/s from document management

## Getting started

### Prerequisites

- [JDK 21](https://www.oracle.com/java)
- [Docker](https://www.docker.com)

## Usage

Just include the library as your dependency and you will be to use the client class. Health check for DM service is provided as well.

Components provided by this library will get automatically configured in a Spring context if `document_management.url` configuration property is defined and does not equal `false`.

You will need to provide a Bean of type `RestTemplate` for the library to use.

### Spring Boot 4 and Jackson

This library targets Spring Boot 4 and currently uses a **temporary Jackson 2 bridge**:

- `spring-boot-jackson2`
- `feign-jackson` with `com.fasterxml.jackson.databind.ObjectMapper`

Boot 4’s preferred JSON stack is Jackson 3 (`tools.jackson` / `JsonMapper`). The Jackson 2 types remain because upload parsing and Feign decoding still depend on `feign-jackson`’s Jackson 2 API, and consumers inject a Jackson 2 `ObjectMapper`.

**Removal condition:** replace `feign-jackson` with `feign-jackson3`, switch public APIs to Jackson 3 mappers, then drop `spring-boot-jackson2`. That is a breaking change for consumers.

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
