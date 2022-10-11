package wiki.lever.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import wiki.lever.entity.projection.PathPermissionProjection;
import wiki.lever.modal.constant.GlobalConfigKey;
import wiki.lever.service.AuthorizationService;
import wiki.lever.service.SysPermissionService;

import java.util.List;
import java.util.Set;

import static wiki.lever.context.DatasourceCacheContextHolder.GlobalConfigHolder.getValue;

/**
 * 2022/10/7 19:24:47
 *
 * @author yue
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"authorization"})
public class AuthorizationServiceImpl implements AuthorizationService {

    private final SysPermissionService sysPermissionService;

    @Override
    @Cacheable(value = "service", key = "#root.methodName")
    public List<PathPermissionProjection> anonymousPermission() {
        String anonymousRoleName = getValue(GlobalConfigKey.ANONYMOUS_ROLE_NAME);
        return sysPermissionService.getPermissions(Set.of(anonymousRoleName));
    }
}
