package wiki.lever.entity.converter;

import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.util.Objects;

/**
 * This converter will adapt to the {@link HttpMethod}
 *
 * @author yue
 */
@Converter(autoApply = true)
public class HttpMethodConverter implements AttributeConverter<HttpMethod, String> {

    @Override
    public String convertToDatabaseColumn(HttpMethod httpMethod) {
        return Objects.isNull(httpMethod) ? null : httpMethod.name();
    }

    @Override
    public HttpMethod convertToEntityAttribute(String httpMethod) {
        return StringUtils.isBlank(httpMethod) ? null : HttpMethod.valueOf(httpMethod);
    }
}
