package wiki.lever.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import wiki.lever.base.BaseCacheRepository;
import wiki.lever.base.BaseRepository;
import wiki.lever.context.DatasourceCacheContextHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static wiki.lever.integration.DataSourceInitializeHanlder.loadAllData;
import static wiki.lever.integration.DataSourceInitializeHanlder.noLoadMockData;

class RedisContainer extends GenericContainer<RedisContainer> {
    public RedisContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public RedisContainer() {
        this(DockerImageName.parse("redis:latest"));
    }
}

record DatasourceRepository(String entity, List<Object> data) {
}

record DatasourceData(String method, List<DatasourceRepository> data, List<DatasourceRepository> cache) {
}

/**
 * Init mock data when test run.
 * <p>
 * 2022/9/12 22:19:57
 *
 * @author yue
 */
@Slf4j
public class DataSourceInitializeListener extends AbstractTestExecutionListener {

    private static final RedisContainer redisContainer = new RedisContainer().withExposedPorts(6379);

    private static final JdbcDatabaseContainer<?> datasourceContainer = new MySQLContainerProvider()
            .newInstance("latest");


    @Override
    public void beforeTestClass(@NotNull TestContext testContext) {
        Startables.deepStart(redisContainer, datasourceContainer).join();
        ApplicationContext applicationContext = testContext.getApplicationContext();
        DatasourceCacheContextHolder.setApplicationContext(applicationContext);
        log.info("[integration] Mysql container init in {}:{}", datasourceContainer.getHost(), datasourceContainer.getFirstMappedPort());
        log.info("[integration] Redis container init in {}:{}", redisContainer.getHost(), redisContainer.getFirstMappedPort());
    }

    @Override
    public void beforeTestMethod(@NotNull TestContext testContext) {
        Method testMethod = testContext.getTestMethod();
        Class<?> testClass = testContext.getTestClass();
        if (noLoadMockData(testMethod, testClass)) {
            return;
        }
        String mockDataFilePath = DataSourceInitializeHanlder.getMockDataFilePath(testClass);
        if (StringUtils.isBlank(mockDataFilePath)) {
            log.warn("[Init mock] Can not find mock file {}.", mockDataFilePath);
            return;
        }
        try {
            File file = new ClassPathResource(mockDataFilePath).getFile();
            List<DatasourceData> mockDataList = new ObjectMapper().readerForListOf(DatasourceData.class).readValue(file);
            DatasourceData datasourceData = IterableUtils.find(mockDataList, item -> StringUtils.equalsIgnoreCase(item.method(), targetName(testMethod)));
            if (Objects.isNull(datasourceData)) {
                log.warn("[Init mock] {} do not have mock data.", testMethod.getName());
                return;
            }
            log.info("[Init mock] {} success init mock data.", testMethod.getName());
            loadAllData(testContext, datasourceData);
        } catch (FileNotFoundException e) {
            log.warn("[Init mock] Can not find mock file {}. {}", mockDataFilePath, e);
        } catch (IOException e) {
            log.warn("[Init mock] Mock file {} can not convert from json. {}", mockDataFilePath, e);
        } catch (ClassNotFoundException e) {
            log.warn("[Init mock] Mock datasource type can not find.", e);
        }
    }


    /**
     * Get target json file method.
     *
     * @param testMethod method
     * @return name
     */
    private CharSequence targetName(Method testMethod) {
        DatasourceMockData datasourceMockData = testMethod.getAnnotation(DatasourceMockData.class);
        return Objects.isNull(datasourceMockData)
                ? testMethod.getName()
                : datasourceMockData.value();
    }

    @Override
    public void afterTestMethod(@NotNull TestContext testContext) {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        String[] repositoryNames = applicationContext.getBeanNamesForType(BaseRepository.class);
        // Clear all tables data.
        for (String repositoryName : repositoryNames) {
            BaseRepository<?> repository = applicationContext.getBean(repositoryName, BaseRepository.class);
            repository.deleteAllInBatch();
            log.info("[After mock] Clear table data of {}.", repositoryName);
        }
        String[] cacheRepositoryNames = applicationContext.getBeanNamesForType(BaseCacheRepository.class);
        // Clear all cache data.
        for (String repositoryName : cacheRepositoryNames) {
            BaseCacheRepository<?> repository = applicationContext.getBean(repositoryName, BaseCacheRepository.class);
            repository.deleteAll();
            log.info("[After mock] Clear cache data of {}.", repositoryName);
        }
    }

    @Override
    public void afterTestClass(@NotNull TestContext testContext) {
        // Stop all container.
        if (redisContainer.isRunning()) {
            redisContainer.stop();
        }
        if (datasourceContainer.isRunning()) {
            datasourceContainer.stop();
        }
    }

    public static class DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + datasourceContainer.getJdbcUrl(),
                    "spring.datasource.username=" + datasourceContainer.getUsername(),
                    "spring.datasource.password=" + datasourceContainer.getPassword(),
                    "spring.data.redis.port=" + redisContainer.getFirstMappedPort().toString(),
                    "spring.data.redis.host=" + redisContainer.getHost()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
