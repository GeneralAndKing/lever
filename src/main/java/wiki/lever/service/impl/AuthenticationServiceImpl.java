package wiki.lever.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import wiki.lever.config.security.JwtProperties;
import wiki.lever.config.security.authentication.UserToken;
import wiki.lever.config.security.authentication.UserTokenInfo;
import wiki.lever.entity.QSysPermission;
import wiki.lever.entity.QSysRole;
import wiki.lever.entity.SysPermission;
import wiki.lever.entity.SysUser;
import wiki.lever.repository.SysUserRepository;
import wiki.lever.repository.cache.UserTokenRepository;
import wiki.lever.service.AuthenticationService;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static wiki.lever.context.DatabaseCacheContextHolder.GlobalConfigHolder.getBoolean;
import static wiki.lever.modal.constant.GlobalConfigKey.AUTHENTICATION_ONCE;

/**
 * 2022/9/3 14:43:47
 *
 * @author yue
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final SysUserRepository sysUserRepository;
    private final UserTokenRepository userTokenRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserRepository.findFirstByUsername(username)
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
        Instant now = Instant.now();
        UserToken userToken = generateNewUserToken(user, now);
        Optional<UserToken> existTokenOptional = userTokenRepository.findById(user.getName());
        Boolean onlyOnceOrNoCache = getBoolean(AUTHENTICATION_ONCE) || existTokenOptional.isEmpty();
        if (Boolean.TRUE.equals(onlyOnceOrNoCache)) {
            userTokenRepository.deleteById(user.getName());
            userTokenRepository.save(userToken);
            return userToken;
        }
        UserToken existToken = existTokenOptional.get();
        Instant expiresAt = jwtDecoder.decode(existToken.getRefreshToken()).getExpiresAt();
        if (Objects.isNull(expiresAt) || expiresAt.isBefore(now)) {
            userTokenRepository.deleteById(user.getName());
            userTokenRepository.save(userToken);
            return userToken;
        }
        return existToken;
    }

    private UserToken generateNewUserToken(SysUser user, Instant now) {
        return new UserToken()
                .setAccessToken(buildAccessToken(user, now))
                .setRefreshToken(buildRefreshToken(user, now))
                .setId(user.getName())
                .setSubject(user.getName())
                .setUsername(user.getUsername());
    }

    private String buildRefreshToken(SysUser user, Instant now) {
        return buildToken(user, now,
                now.plus(jwtProperties.getRefreshTokenExpiresTime(), jwtProperties.getRefreshTokenExpiresUnit()),
                claim -> claim.putAll(Collections.emptyMap())
        );
    }

    private String buildAccessToken(SysUser user, Instant now) {
        return buildToken(user, now,
                now.plus(jwtProperties.getAccessTokenExpiresTime(), jwtProperties.getAccessTokenExpiresUnit()),
                claim -> claim.putAll(buildTokenInfo(user))
        );
    }

    private String buildToken(SysUser user, Instant now, Instant expires, Consumer<Map<String, Object>> claimsConsumer) {
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getName())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiresAt(expires)
                .notBefore(now)
                .id(user.getName())
                .claims(claimsConsumer)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    /**
     * Build user token info when user authentication.
     *
     * @return this token info
     */
    @SuppressWarnings("unchecked")
    private Map<String, Objects> buildTokenInfo(SysUser user) {
        Set<String> roleNames = user.getRoleNames();
        UserTokenInfo userTokenInfo = new UserTokenInfo()
                .setSubject(user.getName())
                .setUsername(user.getUsername())
                .setRoles(roleNames)
                .setPermissions(user.getPermissions());
        return new ObjectMapper().convertValue(userTokenInfo, Map.class);
    }
}
