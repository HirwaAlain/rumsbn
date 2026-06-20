package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ComplaintSeverity;

@Converter(autoApply = true)
public class ComplaintSeverityConverter implements AttributeConverter<ComplaintSeverity, String> {

    @Override
    public String convertToDatabaseColumn(ComplaintSeverity attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ComplaintSeverity convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ComplaintSeverity.fromValue(dbData);
    }
}
