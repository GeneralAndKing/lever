package wiki.lever.service;

import wiki.lever.entity.projection.PathPermissionProjection;

import java.util.List;

/**
 * 2022/10/7 19:24:39
 *
 * @author yue
 */
public interface AuthorizationService {

    /**
     * Find anonymous permission list.
     *
     * @return permission list
     */
    List<PathPermissionProjection> anonymousPermission();

}
