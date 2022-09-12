package wiki.lever.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 2022/9/10 20:45:31
 *
 * @author yue
 */
@NoRepositoryBean
public interface BaseRepository<E> extends JpaRepository<E, Long>, QuerydslPredicateExecutor<E> {
}
