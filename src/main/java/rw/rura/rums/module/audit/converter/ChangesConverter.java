package rw.rura.rums.module.audit.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.audit.ChangeDto;

import java.util.Map;

@Converter
public class ChangesConverter implements AttributeConverter<Map<String, ChangeDto>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, ChangeDto>> TYPE_REF =
            new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<String, ChangeDto> attribute) {
        if (attribute == null) return null;
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot serialize audit changes to JSON", e);
        }
    }

    @Override
    public Map<String, ChangeDto> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return MAPPER.readValue(dbData, TYPE_REF);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot deserialize audit changes from JSON", e);
        }
    }
}
