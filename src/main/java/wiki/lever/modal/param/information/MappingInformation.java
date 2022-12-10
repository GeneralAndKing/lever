package wiki.lever.modal.param.information;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 2022/10/15 22:36:40
 *
 * @author yue
 */
@Data
@Accessors(chain = true)
public class MappingInformation {


    private List<MappingItem> mappings;

    private ObjectNode modalSchema;


    public MappingInformation add(MappingItem item) {
        if (CollectionUtils.isEmpty(mappings)) {
            mappings = new ArrayList<>();
        }
        if (Objects.nonNull(item)) {
            mappings.add(item);
        }
        return this;
    }

}
