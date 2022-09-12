package wiki.lever.config.security.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import wiki.lever.entity.SysUser;
import wiki.lever.modal.exception.SystemException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * It will be encode to {@link Jwt}.
 * <p>
 * 2022/9/9 21:22:25
 *
 * @author yue
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenInfo {

    /**
     * User subject.
     *
     * @see SysUser#getName()
     */
    private String subject;

    /**
     * User name.
     *
     * @see SysUser#getUsername()
     */
    private String username;

    /**
     * Current user roles. (It includes parent roles)
     *
     * @see SysUser#getRoleNames()
     */
    private Set<String> roles = Collections.emptySet();

    /**
     * Current user permissions. (It includes parent roles' permissions)
     *
     * @see wiki.lever.service.AuthenticationService#loadUserByUsername(String) set permissions
     * @see SysUser#getPermissions()
     */
    private Map<HttpMethod, List<String>> permissions = Collections.emptyMap();

    /**
     * Build token info from {@link JSONObject}.
     * {@link Jwt#getClaim(String)} method he provided can only obtain the basic type,
     * and the rest will be converted into JSONObject, so it needs to be converted manually.
     *
     * @param jsonObject claim information
     * @return token info
     */
    public static UserTokenInfo fromJsonObject(JSONObject jsonObject) {
        try {
            return new ObjectMapper().readValue(jsonObject.toJSONString(), UserTokenInfo.class);
        } catch (JsonProcessingException e) {
            throw new SystemException(e);
        }
    }

}
