package wiki.lever.integration;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;
import org.testcontainers.utility.DockerImageName;

class RedisContainer extends GenericContainer<RedisContainer> {
    public RedisContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public RedisContainer() {
        this(DockerImageName.parse("redis:latest"));
    }
}

/**
 * 2022/9/12 22:19:57
 *
 * @author yue
 */
@Slf4j
public class DataSourceTestExecutionListener extends AbstractTestExecutionListener {

    private final RedisContainer redisContainer = new RedisContainer().withExposedPorts(6379);

    private final JdbcDatabaseContainer<?> mysqlContainer = new MySQLContainerProvider()
            .newInstance("latest");

    @Override
    public void beforeTestClass(@NotNull TestContext testContext) {
        redisContainer.start();
        mysqlContainer.start();
        log.info("[integration] Mysql container init in {}:{}", mysqlContainer.getHost(), mysqlContainer.getFirstMappedPort());
        log.info("[integration] Redis container init in {}:{}", redisContainer.getHost(), redisContainer.getFirstMappedPort());
        System.getProperties().setProperty("spring.redis.port", redisContainer.getFirstMappedPort().toString());
        System.getProperties().setProperty("spring.redis.host", redisContainer.getHost());
        System.getProperties().setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
        System.getProperties().setProperty("spring.datasource.username", mysqlContainer.getUsername());
        System.getProperties().setProperty("spring.datasource.password", mysqlContainer.getPassword());
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        if (redisContainer.isRunning()) {
            redisContainer.stop();
        }
        if (mysqlContainer.isRunning()) {
            mysqlContainer.stop();
        }
    }
}
