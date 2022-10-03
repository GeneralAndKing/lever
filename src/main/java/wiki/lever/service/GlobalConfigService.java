package wiki.lever.service;

import wiki.lever.entity.GlobalConfig;
import wiki.lever.modal.constant.GlobalConfigKey;

/**
 * 2022/10/3 14:54:29
 *
 * @author yue
 */
public interface GlobalConfigService {

    /**
     * Get global config.
     *
     * @param key config key
     * @return config value
     */
    GlobalConfig getConfig(GlobalConfigKey key);

}
