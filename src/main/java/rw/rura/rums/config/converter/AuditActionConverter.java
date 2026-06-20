package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.AuditAction;

@Converter(autoApply = true)
public class AuditActionConverter implements AttributeConverter<AuditAction, String> {

    @Override
    public String convertToDatabaseColumn(AuditAction attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public AuditAction convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AuditAction.fromValue(dbData);
    }
}
