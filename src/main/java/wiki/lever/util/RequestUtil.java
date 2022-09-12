package wiki.lever.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 2022/09/12 11:38:24
 *
 * @author xy
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestUtil {

    private static final String UNKNOWN_HEADER = "unknown";

    private static final List<String> IP_HEADER_NAMES = List.of(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    );


    /**
     * Get {@link HttpServletRequest} request name from {@code IP_HEADER_NAMES}.
     *
     * @param request http request
     * @return ip address
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return UNKNOWN_HEADER;
        }
        for (String ipHeaderName : IP_HEADER_NAMES) {
            String ipList = request.getHeader(ipHeaderName);
            if (StringUtils.isEmpty(ipList) || StringUtils.equalsIgnoreCase(UNKNOWN_HEADER, ipHeaderName)) {
                continue;
            }
            return StringUtils.substringBefore(ipList, ",");
        }
        return UNKNOWN_HEADER;
    }
}
