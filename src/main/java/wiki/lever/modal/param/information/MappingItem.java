package wiki.lever.modal.param.information;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 2022/10/30 01:02:45
 *
 * @author yue
 */
@Data
@Accessors(chain = true)
public class MappingItem {

    private String path;

    private RequestMethod method;

    private MappingItem.ResponseType responseType;
    private String responseReference;

    private List<RequestInformation> requestReferences;

    @SuppressWarnings("UnusedReturnValue")
    public MappingItem parameterAdd(RequestInformation information) {
        if (CollectionUtils.isEmpty(requestReferences)) {
            requestReferences = new ArrayList<>();
        }
        if (Objects.nonNull(information)) {
            requestReferences.add(information);
        }
        return this;
    }

    /**
     * Request type
     */
    public enum RequestParameterType {
        /**
         * path
         */
        PATH_VARIABLE,
        /**
         * query
         */
        QUERY_PARAMETER,
        /**
         * request body
         */
        REQUEST_BODY,
        /**
         * other
         */
        OTHER
    }

    /**
     * Response type.
     */
    public enum ResponseType {
        /**
         * Object
         */
        OBJECT,
        /**
         * Array
         */
        ARRAY,
        /**
         * No return
         */
        VOID,
        /**
         * Unknown
         */
        UNKNOWN
    }
}
