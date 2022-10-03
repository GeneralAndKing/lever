package wiki.lever.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import wiki.lever.modal.constant.GlobalConfigKey;

/**
 * 2022/10/3 14:59:50
 *
 * @author yue
 */
@Converter(autoApply = true)
public class GlobalConfigKeyConverter implements AttributeConverter<GlobalConfigKey, String> {
    @Override
    public String convertToDatabaseColumn(GlobalConfigKey attribute) {
        return attribute.key();
    }

    @Override
    public GlobalConfigKey convertToEntityAttribute(String dbData) {
        return GlobalConfigKey.fromKey(dbData);
    }
}
