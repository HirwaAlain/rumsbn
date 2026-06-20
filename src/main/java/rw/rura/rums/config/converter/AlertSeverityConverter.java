package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.AlertSeverity;

@Converter(autoApply = true)
public class AlertSeverityConverter implements AttributeConverter<AlertSeverity, String> {

    @Override
    public String convertToDatabaseColumn(AlertSeverity attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public AlertSeverity convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AlertSeverity.fromValue(dbData);
    }
}
