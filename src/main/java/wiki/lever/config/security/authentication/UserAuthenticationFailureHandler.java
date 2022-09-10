package wiki.lever.config.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import wiki.lever.config.security.SecurityConstant;
import wiki.lever.modal.ErrorResponse;

import java.io.IOException;

/**
 * The handler will catch the {@link AuthenticationException}.
 *
 * <p>
 * 2022/9/8 00:14:49
 * </p>
 *
 * @author yue
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class UserAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse("Authentication exception: " + exception.getLocalizedMessage(), exception);
        SecurityConstant.buildResponse(response, HttpStatus.UNAUTHORIZED, errorResponse);
    }

}
