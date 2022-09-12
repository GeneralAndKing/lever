package wiki.lever.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 2022/9/8 22:09:53
 *
 * @author yue
 */
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final EntityManager entityManager;

    /**
     * QueryDSL query factory.
     *
     * @return bean
     */
    @Bean
    JPAQueryFactory jpaQuery(JpaContext jpaContext) {
        return new JPAQueryFactory(entityManager);
    }


    @Bean
    AuditorAware<String> auditorProvider() {
        return ()-> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
