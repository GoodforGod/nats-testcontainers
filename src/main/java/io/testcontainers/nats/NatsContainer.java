package io.testcontainers.nats;

import com.github.dockerjava.api.command.InspectContainerResponse;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.utility.DockerImageName;

/**
 * Nats TestContainer docker container implementation.
 * <p>
 * <a href="https://docs.nats.io/running-a-nats-service/introduction/flags">NATS flags</a>
 * <a href="https://docs.nats.io/running-a-nats-service/nats_docker">NATS docker</a>
 *
 * @author Anton Kurako (GoodforGod)
 * @since 08.09.2025
 */
public class NatsContainer extends GenericContainer<NatsContainer> implements NatsStartable {

    public static final Integer PORT_CLIENT = 4222;
    public static final Integer PORT_ROUTING = 6222;
    public static final Integer PORT_MONITORING = 8222;

    private static final String IMAGE_NAME = "nats";
    private static final DockerImageName IMAGE = DockerImageName.parse(IMAGE_NAME);

    @Nullable
    private String username;
    @Nullable
    private String password;
    @Nullable
    private String token;

    public NatsContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    public NatsContainer(DockerImageName imageName) {
        super(imageName);
        imageName.assertCompatibleWith(IMAGE);
        addExposedPort(PORT_CLIENT);
        addExposedPort(PORT_ROUTING);
        addExposedPort(PORT_MONITORING);
        withStartupTimeout(Duration.ofSeconds(60));
        withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(NatsContainer.class)));
        waitingFor(Wait.forLogMessage(".*Server is ready.*", 1));
    }

    @Override
    protected void configure() {
        super.configure();
    }

    @Override
    protected void containerIsStarting(InspectContainerResponse containerInfo) {
        if (token != null && (username != null || password != null)) {
            throw new IllegalStateException(
                    "Nats container can't have token and username & password authentication simultaneously");
        } else if (username != null && password == null) {
            throw new IllegalStateException("Nats container authorization username is specified without password");
        } else if (username == null && password != null) {
            throw new IllegalStateException("Nats container authorization password is specified without username");
        }

        super.containerIsStarting(containerInfo);
    }

    public NatsContainer withAuthTokenRandom() {
        return withAuthToken(UUID.randomUUID().toString().replace("-", ""));
    }

    public NatsContainer withAuthToken(String token) {
        this.token = Objects.requireNonNull(token);

        final String id = RandomStringUtils.randomAlphanumeric(8);
        List<String> cmd = getCommonCommand("nats-" + id, "nats");
        cmd.add("--auth");
        cmd.add(token);
        withCommand(cmd.toArray(String[]::new));

        return this;
    }

    public NatsContainer withUsernameAndPassword(String username, String password) {
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);

        final String id = RandomStringUtils.randomAlphanumeric(8);
        List<String> cmd = getCommonCommand("nats-" + id, "nats");
        cmd.add("--user");
        cmd.add(username);
        cmd.add("--pass");
        cmd.add(password);
        withCommand(cmd.toArray(String[]::new));

        return this;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    public URI getURI() {
        if (token != null) {
            return URI.create(String.format("nats://%s@%s:%s", token, getHost(), getPort()));
        } else if (username != null && password != null) {
            return URI.create(String.format("nats://%s:%s@%s:%s", username, password, getHost(), getPort()));
        } else {
            return URI.create(String.format("nats://%s:%s", getHost(), getPort()));
        }
    }

    public URI getMonitoringURI() {
        return URI.create(String.format("http://%s:%s/varz", getHost(), getMappedPort(PORT_MONITORING)));
    }

    public int getPort() {
        return getMappedPort(PORT_CLIENT);
    }

    public int getPortRouting() {
        return getMappedPort(PORT_ROUTING);
    }

    public int getPortMonitoring() {
        return getMappedPort(PORT_MONITORING);
    }

    private static List<String> getCommonCommand(String alias, String clusterName) {
        final List<String> cmd = new ArrayList<>();
        cmd.add("--name");
        cmd.add(alias);
        cmd.add("--cluster_name");
        cmd.add(clusterName);
        cmd.add("--cluster");
        cmd.add("nats://0.0.0.0:6222");
        cmd.add("--port");
        cmd.add(String.valueOf(NatsContainer.PORT_CLIENT));
        cmd.add("--http_port");
        cmd.add(String.valueOf(NatsContainer.PORT_MONITORING));
        return cmd;
    }
}
