package wiki.lever.entity.converter;

import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;
import org.springframework.http.HttpMethod;

/**
 * This converter will adapt to the {@link HttpMethod}
 *
 * @author yue
 */
@Converter(autoApply = true)
public class HttpMethodConverter implements AttributeConverter<HttpMethod, String> {

    @Override
    public String convertToDatabaseColumn(HttpMethod httpMethod) {
        return httpMethod.name();
    }

    @Override
    public HttpMethod convertToEntityAttribute(String httpMethod) {
        return HttpMethod.valueOf(httpMethod);
    }
}
