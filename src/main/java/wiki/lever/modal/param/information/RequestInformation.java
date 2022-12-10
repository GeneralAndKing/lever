package wiki.lever.modal.param.information;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 2022/10/15 22:37:11
 *
 * @author yue
 */
@Data
@Accessors(chain = true)
public class RequestInformation {

    private String name;

    private MappingItem.RequestParameterType requestParameterType;

    private String reference;

}