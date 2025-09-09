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
    private static final NatsContainer container = new NatsContainer("nats:2.11-alpine");

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

**No authorization** is set by default in container.

#### Authentication Token

You can run Nats without authentication by specifying with setter.

```java
@Testcontainers
class NatsContainerTests {

    @Container
    private static final NatsContainer container = new NatsContainer()
            .withAuthTokenRandom();

    @Test
    void checkContainerIsRunning() {
        assertTrue(container.isRunning());
    }
}
```

You can also specify your own token:

```java
@Testcontainers
class NatsContainerTests {

    @Container
    private static final NatsContainer container = new NatsContainer()
            .withAuthToken("myToken");

    @Test
    void checkContainerIsRunning() {
        assertTrue(container.isRunning());
    }
}
```

#### Authentication User Password

You can run container with username and password.

```java
@Testcontainers
class NatsContainerTests {

    @Container
    private static final NatsContainer container = new NatsContainer()
            .withUsernameAndPassword("user", "pass");

    @Test
    void checkContainerIsRunning() {
        assertTrue(container.isRunning());
    }
}
```

## Cluster

You can run [NATS cluster](https://docs.nats.io/running-a-nats-service/configuration/clustering) as TestContainers.

Default cluster with 3 nodes is preconfigured for easy usage.

```java
@Testcontainers
class NatsContainerTests {

    @Container
    private static final NatsCluster container = NatsCluster.builder("nats:2.11-alpine").build();

    @Test
    void checkContainerIsRunning() {
        CLUSTER.getHost();
        CLUSTER.getPort();
        CLUSTER.getUser();
        CLUSTER.getPassword();
    }
}
```

### Cluster Builder

You can build cluster with desired size via *NatsClusterBuilder*.

You can check each container type via specified cluster container method.

```java
final NatsCluster cluster = NatsCluster.builder("nats:2.11-alpine")
            .withNodes(5)              // 5 nodes
            .build();
```

## License

This project licensed under the MIT - see the [LICENSE](LICENSE) file for details.
