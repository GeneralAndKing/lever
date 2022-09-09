package wiki.lever.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import wiki.lever.modal.exception.SystemException;

/**
 * 2022/9/9 22:35:32
 *
 * @author yue
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JacksonUtils {

    public static String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SystemException(e);
        }
    }

}
