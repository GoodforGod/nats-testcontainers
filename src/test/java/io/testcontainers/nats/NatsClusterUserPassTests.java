package io.testcontainers.nats;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 08.09.2025
 */
@Testcontainers
class NatsClusterUserPassTests extends AbstractNatsRunner {

    @Container
    private static final NatsCluster container = NatsCluster.builder("nats:2.11-alpine")
            .withUsernameAndPassword("user", "pass")
            .build();

    @Test
    void checkIsRunningAndMonitoringOk() {
        final boolean running = container.isRunning();
        assertTrue(running);

        checkMonitoringOk(container);
    }

    @Test
    void checkIsRunningAndMessageOk() {
        final boolean running = container.isRunning();
        assertTrue(running);

        checkConnectionEstablished(container);
    }
}
