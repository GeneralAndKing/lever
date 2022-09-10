package wiki.lever.config.security.authentication;

import lombok.Data;
import lombok.experimental.Accessors;
import wiki.lever.config.security.JwtConfiguration;
import wiki.lever.entity.SysUser;

/**
 * Provide token information after user authentication. This is a necessary
 * condition for users to access restricted resources. And it mainly consists of two parts:
 * <ol>
 *     <li>{@code accessToken}: Unique authentication credentials used when users access resources.</li>
 *     <li>{@code refreshToken}: Obtain a new valid access token when the user access token expires.</li>
 * </ol>
 * Refresh Token is long-lived, Access Token is short-lived. We use {@link JwtConfiguration#jwtDecoder()}
 * and {@link JwtConfiguration#jwtEncoder()} to decode and encode our identity information excluding sensitive data.
 * For instance password.
 * Please see the detailed comments for specific fields.
 * <p>
 * 2022/9/9 17:38:16
 * </p>
 *
 * @author yue
 * @see JwtConfiguration all configuration about Jwt
 * @see SysUser user information
 */
@Data
@Accessors(chain = true)
public class UserToken {

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
     * Unique authentication credentials used when users access resources,
     * and the token should be short-lived. It will include user information.
     */
    private String accessToken;

    /**
     * Obtain a new valid access token when the user access token expires,
     * and the token should be long-lived. It won't include user information,
     * To be increasing the security of user tokens and only one-time use.
     * <p>
     * On the one hand, users can update the token information imperceptibly.<br />
     * On the other hand, losses caused by stolen tokens can be effectively reduced.
     *
     * <h3>Why need this token?</h3>
     * When the user token is stolen, the thief can share an access token with the user for operation,
     * but the access token is short and will expire at a certain time. Two situations will occur at this time.
     * <ul>
     *     <li>
     *         In the first case, the user requests the token first, and the access token fails to automatically
     *         refresh the token. At this time, both the access token and the refresh token are refreshed again,
     *         and the thief cannot continue to use the token obtained before.
     *     </li>
     *     <li>
     *          In the second case, the thief requests and refreshes the token first. At this time, the user will
     *          have a wrong token when requesting, and the user needs to log in again, so a new token will be generated
     *          when the user login in again.
     *     </li>
     * </ul>
     * <b>IMPORTANT: There can only be one valid token at the same time, that is, after the token is issued,
     * the token can be recycled or invalidated.</b>
     * <p>
     * Of course, you could still argue that thief could once again get access to both refresh and access tokens and
     * repeat the entire story above, potentially leading to a DoS on user, the actual genuine customer,
     * but then again there is nothing like 100% security.
     */
    private String refreshToken;

}
