package wiki.lever.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wiki.lever.entity.SysPermission;

/**
 * 2022/9/2 22:38:15
 *
 * @author yue
 */
@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission, Long> {
}