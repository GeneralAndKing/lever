package wiki.lever.config.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.time.Duration;
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
    private final JwtProperties jwtProperties;

    public JwtConfiguration(JwtProperties jwtProperties) throws JOSEException {
        jwk = new RSAKeyGenerator(2048)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(UUID.randomUUID().toString())
                .generate();
        this.jwtProperties = jwtProperties;
    }

    /**
     * Decode user info from Jwt token.
     *
     * @return Jwt decoder
     * @throws JOSEException {@link RSAKey#toRSAPublicKey()} throws reason.
     */
    @Bean
    public JwtDecoder jwtDecoder() throws JOSEException {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(jwk.toRSAPublicKey()).build();
        // Add validators.
        OAuth2TokenValidator<Jwt> withClockSkew = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ZERO),
                new JwtIssuerValidator(jwtProperties.getIssuer()));
        jwtDecoder.setJwtValidator(withClockSkew);
        return jwtDecoder;
    }

    /**
     * Encode user info to Jwt token.
     *
     * @return Encoder.
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }


}
