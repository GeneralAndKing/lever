package wiki.lever.config.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import wiki.lever.config.security.authentication.UserTokenInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Custom {@link AuthorizationManager} with {@link RequestAuthorizationContext}.
 * <p>
 * 2022/9/10 15:50:39
 * </p>
 *
 * @author yue
 * @see <a href="https://docs.spring.io/spring-security/reference/6.0/servlet/authorization/authorize-http-requests.html">Authorize HttpServletRequests with AuthorizationFilter</a>
 * @see <a href="https://docs.spring.io/spring-security/reference/6.0/servlet/authorization/architecture.html#authz-custom-authorization-manager">Custom Authorization Managers</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        Authentication authenticationDetail = authentication.get();
        if (!(authenticationDetail instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            log.debug("Current authentication information is not JwtAuthenticationToken.");
            return new AuthorizationDecision(false);
        }
        // It must be Jwt.
        Jwt principal = (Jwt) jwtAuthenticationToken.getPrincipal();
        log.info("Access user {}.", principal.getSubject());
        UserTokenInfo detail = UserTokenInfo.fromMap(principal.getClaims());
        return new AuthorizationDecision(check(requestAuthorizationContext, detail));
    }

    /**
     * Check current permission. It will get all permissions from {@link Jwt#getClaim(String)} with user detail field.
     * Access depends on the {@link AntPathRequestMatcher#matcher(HttpServletRequest)}.
     *
     * @param requestAuthorizationContext current request authorization context
     * @param detail                      user Jwt token info
     * @return whether current user can access
     */
    private static boolean check(RequestAuthorizationContext requestAuthorizationContext, UserTokenInfo detail) {
        Map<HttpMethod, List<String>> permissions = detail.getPermissions();
        log.debug("Can access map {}.", permissions);
        HttpServletRequest request = requestAuthorizationContext.getRequest();
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        return permissions.getOrDefault(httpMethod, Collections.emptyList()).stream()
                .anyMatch(permission ->
                        new AntPathRequestMatcher(permission, httpMethod.name()).matcher(request).isMatch());
    }

}
