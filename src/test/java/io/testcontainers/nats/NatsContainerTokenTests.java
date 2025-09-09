package io.testcontainers.nats;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 08.09.2025
 */
@Testcontainers
class NatsContainerTokenTests extends AbstractNatsRunner {

    @Container
    private static final NatsContainer<?> container = new NatsContainer<>("nats:2.11-alpine")
            .withAuthTokenRandom();

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
