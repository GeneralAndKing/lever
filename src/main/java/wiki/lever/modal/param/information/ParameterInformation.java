package wiki.lever.modal.param.information;

import java.util.List;

/**
 * 2022/10/15 22:37:11
 *
 * @author yue
 */
public record ParameterInformation(
        String name, String type,
        MappingInformation.RequestType requestType, List<ParameterInformation> information
) {

}