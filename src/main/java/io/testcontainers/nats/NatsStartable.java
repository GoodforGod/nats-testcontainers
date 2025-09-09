package io.testcontainers.nats;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * Anton Kurako (GoodforGod)
 *
 * @since 09.09.2025
 */
public interface NatsStartable {

    @Nullable
    String getUsername();

    @Nullable
    String getPassword();

    @Nullable
    String getToken();

    URI getURI();

    URI getMonitoringURI();

    int getPort();

    int getPortRouting();

    int getPortMonitoring();
}
