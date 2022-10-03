package wiki.lever.repository;

import org.springframework.stereotype.Repository;
import wiki.lever.base.BaseRepository;
import wiki.lever.entity.SysUser;

import java.util.Optional;

/**
 * 2022/9/2 22:38:15
 *
 * @author yue
 */
@Repository
public interface SysUserRepository extends BaseRepository<SysUser> {

    /**
     * Find user by username.
     *
     * @param username login username
     * @return Optional of user
     */
    Optional<SysUser> findFirstByUsername(String username);

}