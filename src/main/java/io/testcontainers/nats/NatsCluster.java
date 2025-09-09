package io.testcontainers.nats;

import com.github.dockerjava.api.command.InspectContainerResponse;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.utility.DockerImageName;

/**
 * NATS Cluster TestContainer {@link NatsClusterContainer} Builder.
 * <p>
 * <a href="https://docs.nats.io/running-a-nats-service/configuration/clustering">NATS cluster</a>
 *
 * @author Anton Kurako (GoodforGod)
 * @see NatsClusterBuilder
 * @since 08.09.2025
 */
public class NatsCluster implements NatsStartable, Startable, ContainerState {

    public static final class HostAndPort {

        private final String host;
        private final int port;

        private HostAndPort(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String host() {
            return host;
        }

        public int port() {
            return port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            HostAndPort that = (HostAndPort) o;
            return port == that.port && Objects.equals(host, that.host);
        }

        @Override
        public int hashCode() {
            return Objects.hash(host, port);
        }

        @Override
        public String toString() {
            return "[host=" + host + ", port=" + port + ']';
        }
    }

    private final List<NatsClusterContainer<?>> nodes;

    NatsCluster(List<NatsClusterContainer<?>> nodes) {
        this.nodes = List.copyOf(nodes);
    }

    public static NatsClusterBuilder builder(String imageVersion) {
        return new NatsClusterBuilder(DockerImageName.parse(imageVersion));
    }

    public static NatsClusterBuilder builder(DockerImageName imageName) {
        return new NatsClusterBuilder(imageName);
    }

    public List<NatsClusterContainer<?>> getNodes() {
        return nodes;
    }

    @Nullable
    public String getUsername() {
        return nodes.getFirst().getUsername();
    }

    @Nullable
    public String getPassword() {
        return nodes.getFirst().getPassword();
    }

    @Nullable
    public String getToken() {
        return nodes.getFirst().getToken();
    }

    public URI getURI() {
        return nodes.getFirst().getURI();
    }

    public URI getMonitoringURI() {
        return nodes.getFirst().getMonitoringURI();
    }

    public String getHost() {
        return nodes.getFirst().getHost();
    }

    public int getPort() {
        return nodes.getFirst().getPort();
    }

    public int getPortRouting() {
        return nodes.getFirst().getPortRouting();
    }

    public int getPortMonitoring() {
        return nodes.getFirst().getPortMonitoring();
    }

    public List<HostAndPort> getHostsAndPorts() {
        return nodes.stream()
                .map(c -> new HostAndPort(c.getHost(), c.getPort()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getExposedPorts() {
        return nodes.getFirst().getExposedPorts();
    }

    @Override
    public InspectContainerResponse getContainerInfo() {
        return nodes.getFirst().getContainerInfo();
    }

    @Override
    public void start() {
        try {
            CompletableFuture.runAsync(() -> nodes.get(0).start())
                    .thenCompose(_r -> {
                        final CompletableFuture[] otherFutures = nodes.subList(1, nodes.size()).stream()
                                .map(c -> CompletableFuture.runAsync(c::start))
                                .toArray(CompletableFuture[]::new);
                        return CompletableFuture.allOf(otherFutures);
                    })
                    .get(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // do nothing
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void stop() {
        try {
            final CompletableFuture[] otherFutures = nodes.subList(1, nodes.size()).stream()
                    .map(c -> CompletableFuture.runAsync(c::stop))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(otherFutures)
                    .thenRun(() -> nodes.get(0).stop())
                    .get(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // do nothing
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
