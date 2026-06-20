package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.LicenseCategory;

@Converter(autoApply = true)
public class LicenseCategoryConverter implements AttributeConverter<LicenseCategory, String> {

    @Override
    public String convertToDatabaseColumn(LicenseCategory attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public LicenseCategory convertToEntityAttribute(String dbData) {
        return dbData == null ? null : LicenseCategory.fromValue(dbData);
    }
}
