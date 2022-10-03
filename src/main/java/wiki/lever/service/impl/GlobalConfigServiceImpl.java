package wiki.lever.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import wiki.lever.entity.GlobalConfig;
import wiki.lever.modal.constant.GlobalConfigKey;
import wiki.lever.repository.GlobalConfigRepository;
import wiki.lever.service.GlobalConfigService;

/**
 * 2022/10/3 14:55:38
 *
 * @author yue
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"global-config"})
public class GlobalConfigServiceImpl implements GlobalConfigService {

    private final GlobalConfigRepository globalConfigRepository;

    @Override
    @Cacheable(key = "#configKey.key")
    public GlobalConfig getConfig(GlobalConfigKey configKey) {
        return globalConfigRepository
                .findOne(Example.of(new GlobalConfig().setKey(configKey)))
                .orElse(new GlobalConfig().setKey(configKey).setValue(configKey.defaultValue()));
    }
}
