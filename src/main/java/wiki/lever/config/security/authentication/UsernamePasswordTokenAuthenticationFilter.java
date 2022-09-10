package wiki.lever.config.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

import static wiki.lever.config.security.SecurityConstant.AUTHENTICATION_URL;

/**
 * This filter will handle login requests in Json format.
 * <p>
 * Request body like this:
 * </p>
 * <pre>
 * {
 *     "username": "Your username",
 *     "password: "Your password"
 * }
 * </pre>
 *
 * <p>
 * 2022/9/4 21:41:37
 * </p>
 *
 * @author yue
 * @see UsernamePasswordAuthenticationFilter
 */
public final class UsernamePasswordTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * Set filter processes url and authentication handlers.
     */
    public UsernamePasswordTokenAuthenticationFilter(
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler
    ) {
        super(AUTHENTICATION_URL);
        setAuthenticationSuccessHandler(successHandler);
        setAuthenticationFailureHandler(failureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!HttpMethod.POST.matches(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if (notJson(request)) {
            throw new AuthenticationServiceException("Authentication request not supported media type: " + request.getContentType());
        }
        LoginParam loginParam = LoginParam.parseRequest(request);
        Assert.hasText(loginParam.username(), "Username parameter must not be empty or null");
        Assert.hasText(loginParam.password(), "Password parameter must not be empty or null");
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken
                .unauthenticated(loginParam.username(), loginParam.password());
        // Allow subclasses to set the "details" property
        return super.getAuthenticationManager().authenticate(setDetails(request, authRequest));
    }

    /**
     * Check if the request parameter is in json format from {@link org.springframework.http.HttpHeaders#CONTENT_TYPE}.
     * Accept type {@code application/json*}.
     *
     * @param request login request
     * @return not json format return {@code true}, otherwise return {@code false}
     */
    private static boolean notJson(HttpServletRequest request) {
        String contentType = request.getContentType();
        return Objects.isNull(contentType)
                || !MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.parseMediaType(contentType));
    }

    /**
     * Provided so that subclasses may configure what is put into the authentication
     * request's details property.
     *
     * @param request     that an authentication request is being created for
     * @param authRequest the authentication request object that should have its details
     *                    set
     * @return authRequest self
     */
    private Authentication setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(super.authenticationDetailsSource.buildDetails(request));
        return authRequest;
    }
}


/**
 * Login param.
 *
 * @param username from sys_user name field.
 * @param password from sys_user password field.
 * @see wiki.lever.entity.SysUser
 */
record LoginParam(String username, String password) {

    /**
     * Parse param from {@link HttpServletRequest}.
     *
     * @param request Http request
     * @return param
     */
    public static LoginParam parseRequest(HttpServletRequest request) {
        try (BufferedReader reader = request.getReader()) {
            String bodyJson = reader.lines().reduce("", String::concat);
            return new ObjectMapper().readValue(bodyJson, LoginParam.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Authentication request parse error, please use Json body.");
        }
    }
}