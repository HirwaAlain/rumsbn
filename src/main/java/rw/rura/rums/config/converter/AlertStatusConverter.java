package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.AlertStatus;

@Converter(autoApply = true)
public class AlertStatusConverter implements AttributeConverter<AlertStatus, String> {

    @Override
    public String convertToDatabaseColumn(AlertStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public AlertStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AlertStatus.fromValue(dbData);
    }
}
