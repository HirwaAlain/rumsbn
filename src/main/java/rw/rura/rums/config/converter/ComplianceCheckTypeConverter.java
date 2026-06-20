package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ComplianceCheckType;

@Converter(autoApply = true)
public class ComplianceCheckTypeConverter implements AttributeConverter<ComplianceCheckType, String> {

    @Override
    public String convertToDatabaseColumn(ComplianceCheckType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ComplianceCheckType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ComplianceCheckType.fromValue(dbData);
    }
}
