package wiki.lever.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import wiki.lever.config.CacheConfiguration;
import wiki.lever.modal.exception.SystemException;

/**
 * 2022/9/9 22:35:32
 *
 * @author yue
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JacksonUtil {

    /**
     * Wrap Jackson exception to {@link SystemException}.
     *
     * @param obj java object
     * @return json string
     */
    public static String toJson(Object obj) {
        try {
            return CacheConfiguration.getGlobalObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SystemException(e);
        }
    }

}
