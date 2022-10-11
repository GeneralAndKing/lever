package wiki.lever.service;

import wiki.lever.entity.SysPermission;
import wiki.lever.entity.SysUser;
import wiki.lever.entity.projection.PathPermissionProjection;

import java.util.List;
import java.util.Set;

/**
 * 2022/10/7 20:19:12
 *
 * @author yue
 */
public interface SysPermissionService {
    /**
     * Query all permissions from role names.
     *
     * @param roleNames user role names
     * @return a permission map of {@link SysPermission#getMethod()} and {@link SysPermission#getPath()} list
     */
    List<PathPermissionProjection> getPermissions(Set<String> roleNames);

    /**
     * Query {@code sysUser} all permissions from role names.
     * The method only can be use in {@code service}, because it has
     * lazy load from {@link SysUser#getRoles()}.
     *
     * @param sysUser user
     * @return a permission map of {@link SysPermission#getMethod()} and {@link SysPermission#getPath()} list
     */
    default List<PathPermissionProjection> getPermissions(SysUser sysUser) {
        return getPermissions(sysUser.getRoleNames());
    }

}
