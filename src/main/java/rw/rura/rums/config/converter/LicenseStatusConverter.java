package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.LicenseStatus;

@Converter(autoApply = true)
public class LicenseStatusConverter implements AttributeConverter<LicenseStatus, String> {

    @Override
    public String convertToDatabaseColumn(LicenseStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public LicenseStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LicenseStatus.fromValue(dbData);
    }
}
