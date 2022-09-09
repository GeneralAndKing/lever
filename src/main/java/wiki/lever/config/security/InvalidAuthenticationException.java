package wiki.lever.config.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 2022/9/8 00:07:35
 *
 * @author yue
 */
public class InvalidAuthenticationException extends AuthenticationException {
    public InvalidAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidAuthenticationException(String msg) {
        super(msg);
    }
}
