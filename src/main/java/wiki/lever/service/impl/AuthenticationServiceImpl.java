package wiki.lever.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import wiki.lever.config.security.authentication.UserToken;
import wiki.lever.config.security.authentication.UserTokenInfo;
import wiki.lever.entity.SysUser;
import wiki.lever.repository.SysUserRepository;
import wiki.lever.service.AuthenticationService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 2022/9/3 14:43:47
 *
 * @author yue
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final SysUserRepository sysUserRepository;
    private final JwtEncoder jwtEncoder;
    private static final int DEFAULT_ACCESS_TOKEN_HOURS = 12;
    private static final int DEFAULT_REFRESH_TOKEN_HOURS = 24 * 7;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can not find user: " + username));
        // Don't use lazy initialize in entity, we should prepare data in advance.
        Hibernate.initialize(sysUser.getRoles());
        return sysUser;
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
        UserTokenInfo userTokenInfo = UserTokenInfo.buildTokenInfo(user);
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(user.getName())
                .issuer("lever")
                .issuedAt(now)
                .expiresAt(expires)
                .notBefore(now)
                .id(user.getName())
                // TODO: Maybe can not parse.
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
}
