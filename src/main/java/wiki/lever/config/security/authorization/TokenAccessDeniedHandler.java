package wiki.lever.config.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.web.access.AccessDeniedHandler;
import wiki.lever.modal.ErrorResponse;

import java.io.IOException;

import static wiki.lever.config.security.SecurityConstant.buildResponse;


/**
 * The {@link org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler} mainly implemented
 * <a href="https://datatracker.ietf.org/doc/html/rfc6750">RFC 6750</a>. But our application is not an OAuth2 application,
 * so we customize its response to meet our needs.
 * <p>
 * 2022/9/8 20:40:05
 * </p>
 *
 * @author yue
 * @see wiki.lever.modal.ErrorResponse the exception information will be wraped.
 */
public final class TokenAccessDeniedHandler implements AccessDeniedHandler {

    @Setter
    private String realmName;

    /**
     * Collect error details from the provided parameters and format according to RFC
     * 6750, specifically {@code error}, {@code error_description}, {@code error_uri}, and
     * {@code scope}.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        StringBuilder description = new StringBuilder();
        if (request.getUserPrincipal() instanceof AbstractOAuth2TokenAuthenticationToken) {
            description.append(BearerTokenErrorCodes.INSUFFICIENT_SCOPE + ":The request requires higher privileges than provided by the access token.");
        }
        if (this.realmName != null) {
            description.append("realm=").append(realmName);
        }
        buildResponse(response, HttpStatus.FORBIDDEN,
                new ErrorResponse(description.toString(), accessDeniedException));
    }

}
