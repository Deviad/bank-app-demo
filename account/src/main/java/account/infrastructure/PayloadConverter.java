package account.infrastructure;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

import lombok.SneakyThrows;

@Converter
public class PayloadConverter implements AttributeConverter<Map<String, Object>, String> {

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Map<String, Object> object) {
       return MappingUtils.MAPPER.writeValueAsString(object);
    }

    @SneakyThrows
    @Override
    public Map<String, Object> convertToEntityAttribute(String object) {
        return MappingUtils.deserialize(object);
    }

}