package wiki.lever.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import wiki.lever.entity.SysUser;
import wiki.lever.modal.ErrorResponse;
import wiki.lever.service.AuthorizationService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * 2022/9/7 21:59:54
 *
 * @author yue
 */
@Component
@RequiredArgsConstructor
public final class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    private final AuthorizationService authorizationService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SysUser user)) {
            SecurityConstant.buildResponse(response, HttpStatus.FORBIDDEN,
                    new ErrorResponse("User details error.", Collections.singletonList(getClass().getName()))
            );
            return;
        }
        UserToken userToken = authorizationService.buildToken(user);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String responseBody = new ObjectMapper().writeValueAsString(userToken);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(responseBody);
        }
    }
}
