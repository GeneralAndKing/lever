
package wiki.lever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import wiki.lever.context.DatabaseCacheContextHolder;

/**
 * Application starter.
 *
 * @author yue
 */
@SpringBootApplication
public class LeverApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(LeverApplication.class, args);
        DatabaseCacheContextHolder.setApplicationContext(applicationContext);
    }

}