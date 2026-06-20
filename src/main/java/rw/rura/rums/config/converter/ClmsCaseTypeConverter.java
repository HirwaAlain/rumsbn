package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ClmsCaseType;

@Converter(autoApply = true)
public class ClmsCaseTypeConverter implements AttributeConverter<ClmsCaseType, String> {

    @Override
    public String convertToDatabaseColumn(ClmsCaseType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ClmsCaseType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ClmsCaseType.fromValue(dbData);
    }
}
