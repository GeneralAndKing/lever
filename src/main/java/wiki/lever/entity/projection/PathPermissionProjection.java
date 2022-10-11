package wiki.lever.entity.projection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;
import wiki.lever.entity.serialize.HttpMethodSerialize;

/**
 * {@link wiki.lever.service.SysPermissionService} path projection.
 * <p>
 * 2022/10/7 21:44:18
 *
 * @author yue
 */
@Data
@Accessors(chain = true)
public class PathPermissionProjection {

    /**
     *  request uri
     */
    private String path;

    /**
     * request method.
     */
    @JsonSerialize(using = HttpMethodSerialize.class)
    private HttpMethod method;

}
