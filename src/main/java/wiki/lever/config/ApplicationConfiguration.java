package wiki.lever.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import wiki.lever.config.security.JwtProperties;

import java.util.Objects;
import java.util.Optional;

/**
 * 2022/9/8 22:09:53
 *
 * @author yue
 */
@EnableAsync
@EnableCaching
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
@EnableConfigurationProperties({JwtProperties.class})
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
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.isNull(authentication)) {
                return Optional.of("anonymous");
            }
            return Optional.ofNullable(authentication.getName());
        };
    }
}
