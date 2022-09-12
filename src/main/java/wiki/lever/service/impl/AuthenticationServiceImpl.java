package wiki.lever.service.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import wiki.lever.config.security.authentication.UserToken;
import wiki.lever.config.security.authentication.UserTokenInfo;
import wiki.lever.entity.QSysPermission;
import wiki.lever.entity.QSysRole;
import wiki.lever.entity.SysPermission;
import wiki.lever.entity.SysUser;
import wiki.lever.repository.SysUserRepository;
import wiki.lever.service.AuthenticationService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 2022/9/3 14:43:47
 *
 * @author yue
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final SysUserRepository sysUserRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final JwtEncoder jwtEncoder;
    private static final int DEFAULT_ACCESS_TOKEN_HOURS = 12;
    private static final int DEFAULT_REFRESH_TOKEN_HOURS = 24 * 7;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can not find user: " + username));
        return sysUser.setPermissions(getPermissions(sysUser));
    }

    /**
     * Query {@code sysUser} all permissions from role names.
     * The method only can be use in {@code service}, because it has
     * lazy load from {@link SysUser#getRoles()}.
     *
     * @param sysUser user info
     * @return a permission map of {@link SysPermission#getMethod()} and {@link SysPermission#getPath()} list
     */
    private Map<HttpMethod, List<String>> getPermissions(SysUser sysUser) {
        QSysPermission sysPermission = QSysPermission.sysPermission;
        return jpaQueryFactory
                .select(sysPermission.method, sysPermission.path)
                .from(sysPermission)
                .leftJoin(sysPermission.roles, QSysRole.sysRole)
                .where(QSysRole.sysRole.name.in(sysUser.getRoleNames()))
                .fetch().stream()
                .collect(Collectors.groupingBy(x -> x.get(0, HttpMethod.class),
                        Collectors.mapping(x -> x.get(1, String.class), Collectors.toList()))
                );
    }

    @Override
    public UserToken buildToken(SysUser user) {
        return new UserToken()
                .setAccessToken(buildUserAccessToken(user))
                .setRefreshToken(buildUserRefreshToken(user))
                .setSubject(user.getName())
                .setUsername(user.getUsername());
    }

    public String buildUserAccessToken(SysUser user) {
        Instant now = Instant.now();
        Instant expires = now.plus(DEFAULT_ACCESS_TOKEN_HOURS, ChronoUnit.HOURS);
        UserTokenInfo userTokenInfo = buildTokenInfo(user);
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getName())
                .issuer("lever")
                .issuedAt(now)
                .expiresAt(expires)
                .notBefore(now)
                .id(user.getName())
                .claim("detail", userTokenInfo)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    private String buildUserRefreshToken(SysUser user) {
        Instant now = Instant.now();
        Instant expires = now.plus(DEFAULT_REFRESH_TOKEN_HOURS, ChronoUnit.HOURS);
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getName())
                .issuer("lever")
                .issuedAt(now)
                .expiresAt(expires)
                .notBefore(now)
                .id(user.getName())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    /**
     * Build user token info when user authentication.
     *
     * @return this token info
     */
    private UserTokenInfo buildTokenInfo(SysUser user) {
        Set<String> roleNames = user.getRoleNames();
        return new UserTokenInfo()
                .setSubject(user.getName())
                .setUsername(user.getUsername())
                .setRoles(roleNames)
                .setPermissions(user.getPermissions());
    }
}
