package wiki.lever.config.security;

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

    public static UserTokenInfo buildTokenInfo(SysUser user) {
        return new UserTokenInfo(user.getName(), user.getUsername(), user.getRoleNames());
    }

}
