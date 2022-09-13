package wiki.lever.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.support.Repositories;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;
import org.testcontainers.utility.DockerImageName;
import wiki.lever.base.BaseRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

record DatasourceData(String method, List<DatasourceRepository> data) {
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

    private final RedisContainer redisContainer = new RedisContainer().withExposedPorts(6379);

    private final JdbcDatabaseContainer<?> datasourceContainer = new MySQLContainerProvider()
            .newInstance("latest");

    private final static String MOCK_DATA_FILE_SUFFIX = ".json";

    @Override
    public void beforeTestClass(@NotNull TestContext testContext) {
        redisContainer.start();
        datasourceContainer.start();
        log.info("[integration] Mysql container init in {}:{}", datasourceContainer.getHost(), datasourceContainer.getFirstMappedPort());
        log.info("[integration] Redis container init in {}:{}", redisContainer.getHost(), redisContainer.getFirstMappedPort());
        System.getProperties().setProperty("spring.redis.port", redisContainer.getFirstMappedPort().toString());
        System.getProperties().setProperty("spring.redis.host", redisContainer.getHost());
        System.getProperties().setProperty("spring.datasource.url", datasourceContainer.getJdbcUrl());
        System.getProperties().setProperty("spring.datasource.username", datasourceContainer.getUsername());
        System.getProperties().setProperty("spring.datasource.password", datasourceContainer.getPassword());
    }

    @Override
    public void beforeTestMethod(@NotNull TestContext testContext) {
        Method testMethod = testContext.getTestMethod();
        Class<?> testClass = testContext.getTestClass();
        if (noLoadMockData(testMethod, testClass)) {
            return;
        }
        String mockDataFilePath = getMockDataFilePath(testClass);
        if (StringUtils.isBlank(mockDataFilePath)) {
            log.warn("[Init mock] Can not find mock file {}.", mockDataFilePath);
            return;
        }
        try {
            File file = new ClassPathResource(mockDataFilePath).getFile();
            List<DatasourceData> mockDataList = new ObjectMapper().readerForListOf(DatasourceData.class).readValue(file);
            DatasourceData datasourceData = IterableUtils.find(mockDataList, item -> StringUtils.equalsIgnoreCase(item.method(), testMethod.getName()));
            if (Objects.isNull(datasourceData)) {
                log.warn("[Init mock] {} do not have mock data.", testMethod.getName());
                return;
            }
            List<DatasourceRepository> datasourceRepositories = datasourceData.data();
            for (DatasourceRepository datasourceRepository : datasourceRepositories) {
                initData(testContext.getApplicationContext(), datasourceRepository);
            }
        } catch (FileNotFoundException e) {
            log.warn("[Init mock] Can not find mock file {}. {}", mockDataFilePath, e);
        } catch (IOException e) {
            log.warn("[Init mock] Mock file {} can not convert from json. {}", mockDataFilePath, e);
        } catch (ClassNotFoundException e) {
            log.warn("[Init mock] Mock datasource type can not find.", e);
        }
    }

    /**
     * Init mock data from {@link DatasourceRepository}.
     * First, to find the specified repository from {@code repositories},
     * second, load and parse data from json file and convert to list,
     * last, {@link BaseRepository#saveAll(Iterable)} data.
     *
     * @param applicationContext   spring application context
     * @param datasourceRepository mock datasource repository
     * @throws ClassNotFoundException  can not find entity class
     * @throws JsonProcessingException can not parse json string
     */
    private void initData(ApplicationContext applicationContext, DatasourceRepository datasourceRepository) throws ClassNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Class<?> entityClass = Class.forName(datasourceRepository.entity());
        Optional<Object> repositoryFor = new Repositories(applicationContext).getRepositoryFor(entityClass);
        if (repositoryFor.isEmpty()) {
            log.warn("[Init mock] Mock datasource type can not find repository of {}.", entityClass);
            return;
        }
        BaseRepository<?> repository = (BaseRepository<?>) repositoryFor.get();
        List<Object> data = datasourceRepository.data();
        String dataJsonString = objectMapper.writeValueAsString(data);
        repository.saveAll(objectMapper.readerForListOf(entityClass).readValue(dataJsonString));
    }


    /**
     * Check whether the current test method need to initialize the mock data.
     * <ul>
     *     <li>The test class or test method don't have {@link DatasourceMockData}, no load.</li>
     *     <li>The test class or test method have {@link DatasourceNoMockData}, no load.</li>
     * </ul>
     *
     * @param testMethod current test method
     * @param testClass  current test class
     * @return do not load mock data if true.
     */
    private static Boolean noLoadMockData(Method testMethod, Class<?> testClass) {
        DatasourceMockData methodMockData = testMethod.getAnnotation(DatasourceMockData.class);
        DatasourceMockData classMockData = testClass.getAnnotation(DatasourceMockData.class);
        if (ObjectUtils.allNull(methodMockData, classMockData)) {
            return Boolean.TRUE;
        }
        DatasourceNoMockData methodNoMockData = testMethod.getAnnotation(DatasourceNoMockData.class);
        DatasourceNoMockData classNoMockData = testClass.getAnnotation(DatasourceNoMockData.class);
        if (ObjectUtils.anyNotNull(methodNoMockData, classNoMockData)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Get mock data file name from {@link DatasourceMockData}.
     * The default value is current test class name of json,
     * you can custom name by {@link DatasourceMockData#value()}.
     *
     * @param testClass current test class
     * @return file path
     */
    private static String getMockDataFilePath(Class<?> testClass) {
        DatasourceMockData datasourceMockData = testClass.getAnnotation(DatasourceMockData.class);
        if (Objects.isNull(datasourceMockData)) {
            return null;
        }
        String fileName = datasourceMockData.value();
        if (StringUtils.isBlank(fileName)) {
            fileName = testClass.getSimpleName();
        }
        if (!StringUtils.endsWithIgnoreCase(fileName, MOCK_DATA_FILE_SUFFIX)) {
            fileName += MOCK_DATA_FILE_SUFFIX;
        }
        String packageName = StringUtils.replace(testClass.getPackageName(), "wiki.lever", "mock");
        String filePath = StringUtils.replace(packageName, ".", File.separator) + File.separator;
        return filePath + fileName;
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
}
