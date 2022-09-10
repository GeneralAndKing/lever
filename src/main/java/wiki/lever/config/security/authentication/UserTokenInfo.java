package wiki.lever.config.security.authentication;

import wiki.lever.entity.SysUser;

import java.util.Set;

/**
 * 2022/9/9 21:22:25
 *
 * @author yue
 */
public record UserTokenInfo(
        String subject,
        String username,
        Set<String> roles
) {

    /**
     * Build user token info when user authentication.
     *
     * @return this token info
     */
    public static UserTokenInfo buildTokenInfo(SysUser user) {
        return new UserTokenInfo(user.getName(), user.getUsername(), user.getRoleNames());
    }

}
