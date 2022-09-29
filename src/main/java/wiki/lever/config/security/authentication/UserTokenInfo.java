package wiki.lever.config.security.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import wiki.lever.entity.SysUser;

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
@JsonIgnoreProperties(ignoreUnknown = true)
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
     * Build token info from {@code JSONObject}.
     * {@link Jwt#getClaim(String)} method he provided can only obtain the basic type,
     * and the rest will be converted into JSONObject, so it needs to be converted manually.
     *
     * @param map claim information
     * @return token info
     */
    public static UserTokenInfo fromMap(Map<String, Object> map) {
        return new ObjectMapper().registerModule(new JavaTimeModule())
                .convertValue(map, UserTokenInfo.class);
    }

}
