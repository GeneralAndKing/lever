package wiki.lever.modal.param.information;

import java.util.List;

/**
 * 2022/10/15 22:38:17
 *
 * @author yue
 */
public record ResponseResult(
        String name, String type, List<ParameterInformation> information
) {
}
