package wiki.lever.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Predicates;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * When throw an error in the program, we usually only focus on two things.
 * <ol>
 *     <li><b>Why throw it this error?</b> Let users and developers know the cause of the error.</li>
 *     <li><b>Where throw the error?</b> Let developers quickly locate error code.</li>
 * </ol>
 * <p>
 * So we just need to express the above two meanings clearly.
 * No need for complex and difficult to maintain logic code.（eg: 900, 1000 or more）
 * <br />
 * And the {@link HttpStatus} code is enough for us.
 *
 * <p>
 * 2022/9/7 23:22:47
 * </p>
 *
 * @author yue
 * @see org.springframework.http.HttpStatus Enumeration of HTTP status codes
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7231#section-6">Response Status Codes</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ErrorResponse {

    /**
     * The error location will be format.
     * e.g: "wiki.lever.config.security.LoginParam#parseRequest:120"
     */
    private static final String LOCATION_FORMAT = "%s#%s:%s";

    /**
     * The max stack length.
     */
    private static final int MAX_ERROR_COUNT = 5;

    /**
     * Set exception.It will use {@link Exception#getLocalizedMessage()} set to description by default.
     */
    public ErrorResponse(Exception exception) {
        setDescription(exception.getLocalizedMessage());
        setException(exception);
    }

    /**
     * Custom error description.
     *
     * @param description custom description
     * @param exception   throw exception
     */
    public ErrorResponse(String description, Exception exception) {
        this.description = description;
        setException(exception);
    }

    /**
     * <h3>Why throw it this error?</h3>
     * Error description should be very easy to understand.
     */
    private String description;

    /**
     * <h3>Where throw the error?</h3>
     * Error should help developers locate problems.
     */
    private List<String> error;

    /**
     * Set the exception throwing position.
     *
     * @param exception throw exception
     * @return response
     */
    @SuppressWarnings("UnusedReturnValue")
    public ErrorResponse setException(Exception exception) {
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length == 0) {
            return setError(Collections.singletonList(exception.getClass().toString()));
        }
        List<String> errorLocation = packageErrorLocation(stackTrace);
        if (CollectionUtils.isEmpty(errorLocation)) {
            return setError(errorLocation(stackTrace));
        }
        return setError(errorLocation);
    }

    /**
     * Get the package data of the stack.
     *
     * @param stackTrace come from exception stack trace
     * @return append to string
     */
    private static List<String> packageErrorLocation(StackTraceElement[] stackTrace) {
        return convertToString(stackTrace,
                stackTraceElement -> StringUtils.contains(stackTraceElement.getClassName(), "wiki.lever"));
    }

    /**
     * Get the data of the stack. The quantity depends on {@code MAX_ERROR_COUNT}.
     *
     * @param stackTrace come from exception stack trace
     * @return append to string
     */
    private static List<String> errorLocation(StackTraceElement[] stackTrace) {
        return convertToString(stackTrace, Predicates.isTrue());
    }

    /**
     * {@link StackTraceElement} array convert to string.
     *
     * @param stackTrace exception stack trace array
     * @param condition  condition
     * @return the number of {@code MAX_ERROR_COUNT} trace
     */
    private static List<String> convertToString(StackTraceElement[] stackTrace, Predicate<StackTraceElement> condition) {
        return Arrays.stream(stackTrace)
                .limit(MAX_ERROR_COUNT)
                .filter(condition)
                .map(stackTraceElement ->
                        String.format(
                                LOCATION_FORMAT,
                                stackTraceElement.getClassName(),
                                stackTraceElement.getMethodName(),
                                stackTraceElement.getLineNumber()
                        )
                ).toList();
    }

}
