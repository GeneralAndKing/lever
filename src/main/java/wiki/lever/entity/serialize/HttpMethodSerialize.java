package wiki.lever.entity.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.http.HttpMethod;

import java.io.IOException;

/**
 * 2022/9/12 17:44:12
 *
 * @author yue
 */
public class HttpMethodSerialize extends StdSerializer<HttpMethod> {

    public HttpMethodSerialize() {
        super(HttpMethod.class);
    }

    @Override
    public void serialize(HttpMethod value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.name());
    }
}
