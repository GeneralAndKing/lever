package wiki.lever.modal.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * 2022/10/3 14:52:22
 *
 * @author yue
 */
@Getter
@RequiredArgsConstructor
@Accessors(chain = true, fluent = true)
public enum GlobalConfigKey {
    /**
     *
     */
    AUTHENTICATION_ONCE("true");


    /**
     * Config default value.
     */
    private final String defaultValue;

    /**
     * Get config key from key string.
     *
     * @param key key string
     * @return config key
     */
    public static GlobalConfigKey fromKey(String key) {
        for (GlobalConfigKey value : values()) {
            if (StringUtils.equalsIgnoreCase(value.key(), key)) {
                return value;
            }
        }
        return null;
    }

    @JsonValue
    public String key() {
        return name().toLowerCase();
    }

}
