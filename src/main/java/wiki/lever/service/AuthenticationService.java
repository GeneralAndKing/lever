package wiki.lever.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import wiki.lever.config.security.authentication.UserAuthenticationSuccessHandler;
import wiki.lever.config.security.authentication.UserToken;
import wiki.lever.entity.SysUser;

/**
 * 2022/9/3 14:43:17
 *
 * @author yue
 */
public interface AuthenticationService extends UserDetailsService {

    /**
     * Build user token info when user authentication success.
     *
     * @param user user authentication success
     * @return user token
     * @see UserAuthenticationSuccessHandler
     */
    UserToken buildToken(SysUser user);

}
