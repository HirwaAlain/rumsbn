package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.Province;

@Converter(autoApply = true)
public class ProvinceConverter implements AttributeConverter<Province, String> {

    @Override
    public String convertToDatabaseColumn(Province attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Province convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Province.fromValue(dbData);
    }
}
