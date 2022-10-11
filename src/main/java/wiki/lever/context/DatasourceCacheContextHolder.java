package wiki.lever.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import wiki.lever.entity.GlobalConfig;
import wiki.lever.modal.constant.GlobalConfigKey;
import wiki.lever.service.GlobalConfigService;

/**
 * 2022/10/3 15:38:09
 *
 * @author yue
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatasourceCacheContextHolder {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        DatasourceCacheContextHolder.applicationContext = applicationContext;
    }

    /**
     * Get global config holder.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GlobalConfigHolder {

        public static GlobalConfig getConfig(GlobalConfigKey key) {
            return getService().getConfig(key);
        }

        public static Boolean getBoolean(GlobalConfigKey key) {
            return getConfig(key).getBooleanValue();
        }

        public static Integer getInt(GlobalConfigKey key) {
            return getConfig(key).getIntValue(key);
        }

        public static String getValue(GlobalConfigKey key) {
            return getConfig(key).getValue();
        }

        private static GlobalConfigService getService() {
            return getApplicationContext().getBean(GlobalConfigService.class);
        }

    }
}
