package wiki.lever.service.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wiki.lever.entity.QSysPermission;
import wiki.lever.entity.QSysRole;
import wiki.lever.entity.projection.PathPermissionProjection;
import wiki.lever.service.SysPermissionService;

import java.util.List;
import java.util.Set;

/**
 * 2022/10/7 20:19:19
 *
 * @author yue
 */
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final JPAQueryFactory jpaQueryFactory;


    public List<PathPermissionProjection> getPermissions(Set<String> roleNames) {
        QSysPermission sysPermission = QSysPermission.sysPermission;
        return jpaQueryFactory
                .select(Projections.fields(PathPermissionProjection.class,
                        sysPermission.path, sysPermission.method))
                .from(sysPermission)
                .leftJoin(sysPermission.roles, QSysRole.sysRole)
                .where(QSysRole.sysRole.name.in(roleNames))
                .fetch();
    }


}
