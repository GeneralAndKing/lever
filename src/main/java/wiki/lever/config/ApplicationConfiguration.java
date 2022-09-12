package wiki.lever.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

/**
 * 2022/9/8 22:09:53
 *
 * @author yue
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final EntityManager entityManager;

    /**
     * QueryDSL query factory.
     *
     * @return bean
     */
    @Bean
    JPAQueryFactory jpaQuery() {
        return new JPAQueryFactory(entityManager);
    }


    @Bean
    AuditorAware<String> auditorProvider() {
        return ()-> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                return Optional.of(jwtAuthenticationToken.getName());
            }
            return Optional.empty();
        };
    }
}
