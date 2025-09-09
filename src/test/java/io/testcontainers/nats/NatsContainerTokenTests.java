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
