package io.testcontainers.nats;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Anton Kurako (GoodforGod)
 *
 * @since 09.09.2025
 */
abstract class AbstractNatsRunner {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    void checkMonitoringOk(NatsStartable container) {
        var uri = container.getMonitoringURI();

        try {
            HttpRequest requiest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(requiest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode());
        } catch (Exception e) {
            throw new IllegalStateException("Failed connection for URI: " + uri, e);
        }
    }

    void checkConnectionEstablished(NatsStartable container) {
        var uri = container.getURI();

        var opsBuilder = Options.builder()
                .server(uri.toString())
                .reconnectWait(Duration.ofMillis(500))
                .maxReconnects(3)
                .connectionTimeout(Duration.ofSeconds(10));

        if (container.getToken() != null) {
            opsBuilder.token(container.getToken().toCharArray());
        } else if (container.getUsername() != null && container.getPassword() != null) {
            opsBuilder.userInfo(container.getUsername(), container.getPassword());
        }

        try (Connection connection = Nats.connect(opsBuilder.build())) {
            connection.publish("subj", "subjValue".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed connection for URI: " + uri, e);
        }
    }
}
