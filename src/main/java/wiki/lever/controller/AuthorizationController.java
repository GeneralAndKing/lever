package wiki.lever.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wiki.lever.config.security.authentication.UserTokenInfo;

import static org.springframework.http.ResponseEntity.ok;

/**
 * 2022/9/14 14:58
 *
 * @author yue
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/authorization")
public class AuthorizationController {


    /**
     * Parse current token info.
     *
     * @param jwtAuthenticationToken current token info
     * @return user token info
     */
    @GetMapping("/tokenInfo")
    public HttpEntity<UserTokenInfo> tokenInfo(JwtAuthenticationToken jwtAuthenticationToken) {
        Jwt principal = (Jwt) jwtAuthenticationToken.getPrincipal();
        return ok(UserTokenInfo.fromMap(principal.getClaims()));
    }

}
