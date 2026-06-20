package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ComplianceStatus;

@Converter(autoApply = true)
public class ComplianceStatusConverter implements AttributeConverter<ComplianceStatus, String> {

    @Override
    public String convertToDatabaseColumn(ComplianceStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ComplianceStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ComplianceStatus.fromValue(dbData);
    }
}
