package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ComplaintStatus;

@Converter(autoApply = true)
public class ComplaintStatusConverter implements AttributeConverter<ComplaintStatus, String> {

    @Override
    public String convertToDatabaseColumn(ComplaintStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ComplaintStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ComplaintStatus.fromValue(dbData);
    }
}
