package io.testcontainers.nats;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

/**
 * Nats Cluster TestContainer implementation.
 *
 * @author Anton Kurako (GoodforGod)
 * @see NatsClusterBuilder
 * @since 08.09.2025
 */
public class NatsClusterContainer extends NatsContainer {

    public enum NodeType {

        LEADER("leader"),
        NODE("node");

        private final String alias;

        NodeType(String alias) {
            this.alias = alias;
        }

        public String alias(String clusterId, int number) {
            return (this.equals(LEADER))
                    ? "nats-leader-" + clusterId
                    : "nats-node-" + number + "-" + clusterId;
        }
    }

    static class Auth {

        @Nullable
        String username;
        @Nullable
        String password;
        @Nullable
        String token;
    }

    private final NodeType type;
    private final String alias;

    private NatsClusterContainer(DockerImageName dockerImageName, NodeType type, String alias) {
        super(dockerImageName);
        this.type = type;
        this.alias = alias;
        withLogConsumer(new Slf4jLogConsumer(
                LoggerFactory.getLogger(NatsClusterContainer.class.getCanonicalName() + " [" + alias + "]")));
    }

    public NodeType getType() {
        return type;
    }

    public String getAlias() {
        return alias;
    }

    static NatsClusterContainer master(DockerImageName image, String clusterId, Auth auth) {
        final String alias = NodeType.LEADER.alias(clusterId, 0);
        final List<String> cmd = getCommonCommand(alias, clusterId, auth);

        var container = new NatsClusterContainer(image, NodeType.LEADER, alias);
        if (auth.token != null) {
            container.withAuthToken(auth.token);
        }
        if (auth.username != null && auth.password != null) {
            container.withUsernameAndPassword(auth.username, auth.password);
        }
        return (NatsClusterContainer) container
                .withNetworkAliases(alias)
                .withCommand(cmd.toArray(new String[0]));
    }

    static NatsClusterContainer
            slave(DockerImageName image, String clusterId, Auth auth, int nodeNumber, String clusterAlias) {
        final String defaultClusterUserName = "ruser";
        final String defaultClusterUserPassword = "T0pS3cr3t";

        final String alias = NodeType.NODE.alias(clusterId, nodeNumber);
        final List<String> cmd = getCommonCommand(alias, clusterId, auth);
        cmd.add("--connect_retries");
        cmd.add("10");
        cmd.add("--routes");
        cmd.add(String.format("nats://%s:%s@%s:%s",
                defaultClusterUserName, defaultClusterUserPassword, clusterAlias, NatsContainer.PORT_ROUTING));

        var container = new NatsClusterContainer(image, NodeType.LEADER, alias);
        if (auth.token != null) {
            container.withAuthToken(auth.token);
        }
        if (auth.username != null && auth.password != null) {
            container.withUsernameAndPassword(auth.username, auth.password);
        }
        return (NatsClusterContainer) container
                .withNetworkAliases(alias)
                .withCommand(cmd.toArray(new String[0]));
    }

    private static List<String> getCommonCommand(String alias, String clusterId, Auth auth) {
        final List<String> cmd = new ArrayList<>();
        cmd.add("--name");
        cmd.add(alias);
        cmd.add("--cluster_name");
        cmd.add("nats-" + clusterId);
        cmd.add("--cluster");
        cmd.add("nats://0.0.0.0:6222");
        cmd.add("--port");
        cmd.add(String.valueOf(NatsContainer.PORT_CLIENT));
        cmd.add("--http_port");
        cmd.add(String.valueOf(NatsContainer.PORT_MONITORING));
        return cmd;
    }

    @Override
    public String getContainerName() {
        return super.getContainerName() + "[" + alias + "]";
    }
}
