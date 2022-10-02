package wiki.lever.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import wiki.lever.config.security.authentication.TokenAuthenticationEntryPoint;
import wiki.lever.config.security.authentication.UsernamePasswordTokenAuthenticationFilter;
import wiki.lever.config.security.authorization.TokenAccessDeniedHandler;

/**
 * In Spring Security 5.7.0-M2+, spring security <a href="https://github.com/spring-projects/spring-security/issues/10822">deprecated</a> the
 * {@code WebSecurityConfigurerAdapter}, as spring encourage users to move towards a component-based security configuration.
 *
 * <p>
 * 2022/9/3 13:34:39
 * </p>
 *
 * @author yue
 * @see <a href="https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter#ldap-authentication">Spring Security without the WebSecurityConfigurerAdapter</a>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * We should manual register {@link AuthenticationManager}.
     */
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        UsernamePasswordTokenAuthenticationFilter authenticationFilter =
                new UsernamePasswordTokenAuthenticationFilter(authenticationSuccessHandler, authenticationFailureHandler);
        authenticationFilter.setAuthenticationManager(authenticationConfiguration.getAuthenticationManager());
        TokenAuthenticationEntryPoint authenticationEntryPoint = new TokenAuthenticationEntryPoint();
        TokenAccessDeniedHandler accessDeniedHandler = new TokenAccessDeniedHandler();
        return http
                .csrf().disable()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2ResourceServer(config -> config.authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt()
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }


    /**
     * Create password encoder. It will encode ande decode {@link wiki.lever.entity.SysUser} password field.
     *
     * @see <a href="https://docs.spring.io/spring-security/reference/6.0.0-M6/features/authentication/password-storage.html">Password Storage</a>
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Converter jwt scope to user current roles.
     *
     * @return converter
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
