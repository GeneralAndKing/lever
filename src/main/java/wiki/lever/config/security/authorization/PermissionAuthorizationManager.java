package wiki.lever.config.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import wiki.lever.config.security.authentication.UserTokenInfo;
import wiki.lever.entity.projection.PathPermissionProjection;
import wiki.lever.service.AuthorizationService;

import java.util.List;
import java.util.Optional;
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

    private final AuthorizationService authorizationService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        Authentication authenticationDetail = authentication.get();
        if (authenticationDetail instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            log.debug("Current authentication information is JwtAuthenticationToken.");
            return jwtAuthenticationCheck(jwtAuthenticationToken, requestAuthorizationContext.getRequest());
        }
        if (authenticationDetail instanceof AnonymousAuthenticationToken) {
            log.debug("Current authentication information is AnonymousAuthenticationToken.");
            return anonymousAuthenticationCheck(requestAuthorizationContext.getRequest());
        }
        return new AuthorizationDecision(false);
    }

    /**
     * Get anonymous permissions and check current request.
     *
     * @param request current request
     * @return decision
     */
    private AuthorizationDecision anonymousAuthenticationCheck(HttpServletRequest request) {
        return getAuthorizationDecision(request, authorizationService.anonymousPermission());
    }

    /**
     * Check current permission. It will get all permissions from {@link Jwt#getClaim(String)} with user detail field.
     *
     * @param jwtAuthenticationToken current user token information
     * @param request                servlet request
     * @return whether current user can access
     */
    private AuthorizationDecision jwtAuthenticationCheck(JwtAuthenticationToken jwtAuthenticationToken, HttpServletRequest request) {
        Jwt principal = (Jwt) jwtAuthenticationToken.getPrincipal();
        log.info("Access user {}.", principal.getSubject());
        UserTokenInfo detail = UserTokenInfo.fromMap(principal.getClaims());
        return getAuthorizationDecision(request, detail.getPermissions());
    }

    /**
     * Check access map. Access uri depends on the {@link AntPathRequestMatcher#matcher(HttpServletRequest)}.
     *
     * @param request     current request
     * @param permissions current user's permissions
     * @return decision
     */
    private AuthorizationDecision getAuthorizationDecision(HttpServletRequest request, List<PathPermissionProjection> permissions) {
        log.debug("Can access map {}.", permissions);
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        Optional<PathPermissionProjection> projection =
                Optional.ofNullable(IteratorUtils.find(permissions.listIterator(),
                        permission -> new AntPathRequestMatcher(permission.getPath(), httpMethod.name())
                                .matcher(request).isMatch()));
        return new AuthorizationDecision(projection.isPresent());
    }
}
