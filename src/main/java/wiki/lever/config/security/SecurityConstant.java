package wiki.lever.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wiki.lever.modal.ErrorResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;


/**
 * 2022/9/7 21:43:32
 *
 * @author yue
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstant {

    /**
     * User authentication token url.
     */
    public static final String AUTHENTICATION_URL = "/authentication/token";
    public static final String ANONYMOUS = "anonymous";

    /**
     * The first set the <a href="https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Type">Http header Content-Type</a> to {@code application/json},
     * then set the specified status code and character encoding. The last convert {@link ErrorResponse} to Json string.
     *
     * @param response      come from http servlet response
     * @param httpStatus    specified status code
     * @param errorResponse response body
     * @throws IOException if the convert fail
     */
    public static void buildResponse(HttpServletResponse response, HttpStatus httpStatus, ErrorResponse errorResponse) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String responseBody = new ObjectMapper().writeValueAsString(errorResponse);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(responseBody);
        }
    }
}
