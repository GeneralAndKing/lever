package wiki.lever.modal.param.information;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 2022/10/15 22:36:40
 *
 * @author yue
 */
public record MappingInformation(
        String path, RequestMethod method,
        ResponseType responseType,
        List<ParameterInformation> requestParameterInformation,
        List<ResponseResult> responseResult
) {

    public enum RequestType {
        PATH_VARIABLE,
        QUERY_PARAMETER,
        REQUEST_BODY,
        FORM,
        OTHER
    }

    public enum ResponseType {
        OBJECT,
        COLLECTION,
        VOID
    }

}
