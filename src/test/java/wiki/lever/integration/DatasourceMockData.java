package wiki.lever.integration;

import org.springframework.test.context.TestContext;

import java.lang.annotation.*;

/**
 * Mock mysql data, it will load the same SQL file as the method name if value is empty.
 * <p>
 * 2022/9/13 10:19
 *
 * @author yue
 * @see DataSourceInitializeListener#beforeTestMethod(TestContext)
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatasourceMockData {

    /**
     * Mock file name, default current class name with json file.
     *
     * @return file name
     */
    String value() default "";

}
