package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.AuditModule;

@Converter(autoApply = true)
public class AuditModuleConverter implements AttributeConverter<AuditModule, String> {

    @Override
    public String convertToDatabaseColumn(AuditModule attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public AuditModule convertToEntityAttribute(String dbData) {
        return dbData == null ? null : AuditModule.fromValue(dbData);
    }
}
