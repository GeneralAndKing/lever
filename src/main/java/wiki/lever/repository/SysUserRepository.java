package wiki.lever.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wiki.lever.entity.SysUser;

import java.util.Optional;

/**
 * 2022/9/2 22:38:15
 *
 * @author yue
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    /**
     * Find user by username.
     *
     * @param username login username
     * @return Optional of user
     */
    Optional<SysUser> findByUsername(String username);

}