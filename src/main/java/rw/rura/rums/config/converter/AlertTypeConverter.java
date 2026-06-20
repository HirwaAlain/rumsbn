package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.AlertType;

@Converter(autoApply = true)
public class AlertTypeConverter implements AttributeConverter<AlertType, String> {

    @Override
    public String convertToDatabaseColumn(AlertType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public AlertType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AlertType.fromValue(dbData);
    }
}
