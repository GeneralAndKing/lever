package wiki.lever.config.security.authorization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * 2022/9/10 15:50:39
 *
 * @author yue
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {


    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        Authentication authenticationDetail = authentication.get();
        if (!(authenticationDetail instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            log.debug("Current authentication information is not JwtAuthenticationToken.");
            return new AuthorizationDecision(false);
        }
        // It must be Jwt.
        Jwt principal = (Jwt) jwtAuthenticationToken.getPrincipal();
        log.info("Access user {}.", principal.getSubject());
        return new AuthorizationDecision(true);
    }

}
