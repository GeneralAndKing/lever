package wiki.lever.base;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 2022/9/10 20:45:31
 *
 * @author yue
 */
@NoRepositoryBean
public interface BaseCacheRepository<E> extends CrudRepository<E, String> {

}
