package wiki.lever.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.test.context.TestContext;
import wiki.lever.base.BaseCacheRepository;
import wiki.lever.base.BaseRepository;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 2022/10/3 22:14:42
 *
 * @author yue
 */
@Slf4j
public class DataSourceInitializeHanlder {

    private final static String MOCK_DATA_FILE_SUFFIX = ".json";

    /**
     * Load all data from {@code MOCK_DATA_FILE_SUFFIX} file.
     *
     * @param testContext    current context
     * @param datasourceData data
     * @throws ClassNotFoundException,JsonProcessingException load exception
     */
    public static void loadAllData(@NotNull TestContext testContext, DatasourceData datasourceData) throws ClassNotFoundException, JsonProcessingException {
        List<DatasourceRepository> data = datasourceData.data();
        if (CollectionUtils.isNotEmpty(data)) {
            for (DatasourceRepository datasourceRepository : datasourceData.data()) {
                DataSourceInitializeHanlder.initDatasourceData(testContext.getApplicationContext(), datasourceRepository, false);
            }
        }
        List<DatasourceRepository> cache = datasourceData.cache();
        if (CollectionUtils.isNotEmpty(cache)) {
            for (DatasourceRepository cacheRepository : cache) {
                DataSourceInitializeHanlder.initDatasourceData(testContext.getApplicationContext(), cacheRepository, true);
            }
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
    public static void initDatasourceData(
            ApplicationContext applicationContext,
            DatasourceRepository datasourceRepository,
            Boolean cache
    ) throws ClassNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Class<?> entityClass = Class.forName(datasourceRepository.entity());
        Optional<Object> repositoryFor = new Repositories(applicationContext).getRepositoryFor(entityClass);
        if (repositoryFor.isEmpty()) {
            log.warn("[Init mock] Mock datasource type can not find repository of {}.", entityClass);
            return;
        }
        List<Object> data = datasourceRepository.data();
        String dataJsonString = objectMapper.writeValueAsString(data);
        JsonNode dataJsonNode = objectMapper.readTree(dataJsonString);
        CollectionType toValueType = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, entityClass);
        if (cache) {
            BaseCacheRepository<?> cacheRepository = (BaseCacheRepository<?>) repositoryFor.get();
            cacheRepository.saveAll(objectMapper.convertValue(dataJsonNode, toValueType));
            return;
        }
        BaseRepository<?> repository = (BaseRepository<?>) repositoryFor.get();
        repository.saveAll(objectMapper.convertValue(dataJsonNode, toValueType));
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
    public static Boolean noLoadMockData(Method testMethod, Class<?> testClass) {
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
    public static String getMockDataFilePath(Class<?> testClass) {
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
}
