package wiki.lever.config.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.web.AuthenticationEntryPoint;
import wiki.lever.config.security.SecurityConstant;
import wiki.lever.modal.ErrorResponse;

import java.io.IOException;


/**
 * An {@link AuthenticationEntryPoint} implementation used to commence authentication.
 * <p>
 * Uses information provided by {@link BearerTokenError} to set HTTP response status code
 * and populate {@code WWW-Authenticate} HTTP header.
 *
 * <p>
 * 2022/9/7 22:39:15
 * </p>
 *
 * @author yue
 */
public final class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse("Authentication exception: " + authException.getLocalizedMessage(), authException);
        SecurityConstant.buildResponse(response, HttpStatus.UNAUTHORIZED, errorResponse);
    }

}
