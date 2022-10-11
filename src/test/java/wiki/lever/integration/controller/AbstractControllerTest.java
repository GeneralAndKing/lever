package wiki.lever.integration.controller;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import wiki.lever.integration.DataSourceInitializeListener;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

/**
 * Spring integration test support for controller.
 * It will init {@link RequestSpecification} and init mock data.
 * <p>
 * 2022/9/28 14:42
 *
 * @author yue
 */
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = DataSourceInitializeListener.DataSourceInitializer.class)
@TestExecutionListeners(
        listeners = DataSourceInitializeListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public abstract class AbstractControllerTest {

    protected RequestSpecification spec;

    @LocalServerPort
    private int port;

    @BeforeEach
    void initRequestSpecification(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setPort(port)
                .addFilter(
                        documentationConfiguration(restDocumentation)
                                .operationPreprocessors()
                                .withResponseDefaults(prettyPrint())
                                .withRequestDefaults(prettyPrint())
                )
                .build();
    }
}
