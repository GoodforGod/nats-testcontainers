# Nats TestContainers

[![Minimum required Java version](https://img.shields.io/badge/Java-11%2B-blue?logo=openjdk)](https://openjdk.org/projects/jdk/11/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.goodforgod/nats-testcontainer/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.goodforgod/nats-testcontainer)
[![GitHub Action](https://github.com/goodforgod/nats-testcontainers/workflows/CI%20Master/badge.svg)](https://github.com/GoodforGod/nats-testcontainers/actions?query=workflow%3A"CI+Master"++)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_nats-testcontainers&metric=alert_status)](https://sonarcloud.io/dashboard?id=GoodforGod_nats-testcontainers)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_nats-testcontainers&metric=coverage)](https://sonarcloud.io/dashboard?id=GoodforGod_nats-testcontainers)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=GoodforGod_nats-testcontainers&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=GoodforGod_nats-testcontainers)

This is [Nats TestContainers](https://nats.io) module for running database as Docker container.

## Dependency :rocket:

**Gradle**
```groovy
testImplementation "io.goodforgod:nats-testcontainers:0.1.0"
```

**Maven**
```xml
<dependency>
    <groupId>io.goodforgod</groupId>
    <artifactId>nats-testcontainers</artifactId>
    <version>0.1.0</version>
    <scope>test</scope>
</dependency>
```

### Testcontainers

- Version 0.1.0+ - build on top of Testcontainers [1.21.3](https://mvnrepository.com/artifact/org.testcontainers/testcontainers/1.21.3)

## Usage

Check [this](https://www.testcontainers.org/test_framework_integration/junit_5/) TestContainers tutorials for **Jupiter / JUnit 5** examples.

Run Nats container *without* authentication.
```java
@Testcontainers
class NatsContainerTests {

    @Container
    private static final NatsContainer<?> container = new NatsContainer<>("nats:2.11-alpine");

    @Test
    void checkContainerIsRunning() {
        assertTrue(container.isRunning());
    }
}
```

## Container

### Up & Running

Container implements *startup strategy* and will be *available to TestContainer framework automatically* when database will be ready for accepting connections.

Check [here](https://www.testcontainers.org/features/startup_and_waits/) for more info about strategies.

### Auth

All authentication options are available as per [Nats Docker description](https://hub.docker.com/_/nats).

*Without authentication or password or random password* configuration is **required** as per [docker image](https://hub.docker.com/_/nats).

#### Without Authentication

You can run Nats without authentication by specifying with setter.

```java
@Testcontainers
class NatsContainerTests {

    @Container
    private static final NatsContainer<?> container = new NatsContainer<>()
            .withoutAuth();

    @Test
    void checkContainerIsRunning() {
        assertTrue(container.isRunning());
    }
}
```

## License

This project licensed under the MIT - see the [LICENSE](LICENSE) file for details.
