package wiki.lever.config.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.util.UUID;

/**
 * The configuration customize:
 * <ul>
 *     <li>{@link JwtDecoder}: It will be used in {@link JwtAuthenticationProvider}.</li>
 *     <li>{@link JwtEncoder}: It will be used to generate user JWT tokens.</li>
 * </ul>
 *
 * <p>
 * 2022/9/7 20:20:43
 * </p>
 *
 * @author yue
 * @see <a href="https://github.com/spring-projects/spring-security-samples/blob/main/servlet/spring-boot/java/jwt/login/src/main/java/example/RestConfig.java">Spring secuirty jwt demo</a>
 * @see <a href="https://docs.spring.io/spring-security/reference/6.0.0-M6/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-architecture">How JWT Authentication Works</a>
 * @see <a href="https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-rsa-signature">JSON Web Token (JWT) with RSA signature</a>
 */
@Configuration
public class JwtConfiguration {

    private final RSAKey jwk;

    public JwtConfiguration() throws JOSEException {
        jwk = new RSAKeyGenerator(2048)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(UUID.randomUUID().toString())
                .generate();
    }

    /**
     * <img src="https://docs.spring.io/spring-security/reference/6.0.0-M6/_images/servlet/oauth2/jwtauthenticationprovider.png" alt="JwtAuthenticationProvider Usage" />
     *
     * @return Jwt decoder
     * @throws JOSEException {@link RSAKey#toRSAPublicKey()} throws reason.
     */
    @Bean
    public JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(jwk.toRSAPublicKey()).build();
    }

    /**
     * Encode user info to Jwt token.
     *
     * @return Encoder.
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }
}
