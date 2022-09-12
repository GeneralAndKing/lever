package wiki.lever.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 2022/9/8 22:09:53
 *
 * @author yue
 */
@Configuration
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

}
