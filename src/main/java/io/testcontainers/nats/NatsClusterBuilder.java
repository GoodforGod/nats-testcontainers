package io.testcontainers.nats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.testcontainers.containers.Network;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.utility.DockerImageName;

/**
 * NATS Cluster TestContainer {@link NatsClusterContainer} Builder.
 * <p>
 * <a href="https://docs.nats.io/running-a-nats-service/configuration/clustering">NATS cluster</a>
 *
 * @author Anton Kurako (GoodforGod)
 * @since 08.09.2025
 */
public class NatsClusterBuilder {

    private static final int NODES_DEFAULT = 3;

    private final DockerImageName image;

    private int nodes = NODES_DEFAULT;
    private final NatsClusterContainer.Auth auth = new NatsClusterContainer.Auth();

    NatsClusterBuilder(DockerImageName image) {
        this.image = image;
    }

    public NatsClusterBuilder withNodes(int coordinatorNodes) {
        this.nodes = coordinatorNodes;
        return this;
    }

    public NatsClusterBuilder withAuthTokenRandom() {
        return withAuthToken(UUID.randomUUID().toString().replace("-", ""));
    }

    public NatsClusterBuilder withAuthToken(String token) {
        this.auth.token = token;
        return this;
    }

    public NatsClusterBuilder withUsernameAndPassword(String username, String password) {
        this.auth.username = username;
        this.auth.password = password;
        return this;
    }

    public NatsCluster build() {
        return build(null);
    }

    public NatsCluster build(@Nullable Network network) {
        return new NatsCluster(buildContainers(network));
    }

    private List<NatsClusterContainer<?>> buildContainers(@Nullable Network network) {
        if (image == null)
            throw new UnsupportedOperationException("Image version can not be empty!");
        if (nodes < 2)
            throw new IllegalArgumentException("Nodes can not be less 2");

        final String clusterId = RandomStringUtils.randomAlphanumeric(8);
        final NatsClusterContainer<?> leader = NatsClusterContainer.master(image, clusterId, auth);

        // Build nodes
        final List<NatsClusterContainer<?>> nodes = new ArrayList<>(this.nodes);
        for (int i = 1; i <= this.nodes; i++) {
            var node = NatsClusterContainer.slave(image, clusterId, auth, i, leader.getAlias())
                    .dependsOn(leader);
            nodes.add(node);
        }

        return Stream.of(List.of(leader), nodes)
                .flatMap(Collection::stream)
                .map(c -> (network != null)
                        ? ((NatsClusterContainer<?>) c.withNetwork(network))
                        : ((NatsClusterContainer<?>) c.withNetwork(Network.SHARED)))
                .collect(Collectors.toUnmodifiableList());
    }
}
