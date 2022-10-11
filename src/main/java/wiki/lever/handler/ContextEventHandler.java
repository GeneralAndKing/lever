package wiki.lever.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import wiki.lever.context.DatasourceCacheContextHolder;

/**
 * 2022/10/7 19:46:10
 *
 * @author yue
 */
@Slf4j
@Component
public class ContextEventHandler {

    @EventListener
    public void handleContextRefreshEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        DatasourceCacheContextHolder.setApplicationContext(applicationContext);
        log.info("DatasourceCacheContextHolder init success.");
    }

}
