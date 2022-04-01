package account.infrastructure;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;

public class MappingUtils {

    public static ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);


    public static Map<String, Object> toMap(Object object) {
        return MAPPER.convertValue(object, new TypeReference<>() {
        });
    }

    @SneakyThrows
    public static Map<String, Object> deserialize(String object) {
        return MAPPER.readValue(object, new TypeReference<>() {
        });
    }

    @SneakyThrows
    public static String serialize(Object object) {
        return MAPPER.writeValueAsString(object);
    }
}
