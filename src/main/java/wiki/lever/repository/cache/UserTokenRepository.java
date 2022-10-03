package wiki.lever.repository.cache;

import org.springframework.stereotype.Repository;
import wiki.lever.base.BaseCacheRepository;
import wiki.lever.config.security.authentication.UserToken;


/**
 * 2022/10/3 16:30:47
 *
 * @author yue
 */
@Repository
public interface UserTokenRepository extends BaseCacheRepository<UserToken> {

}
