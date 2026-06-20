package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ComplaintCategory;

@Converter(autoApply = true)
public class ComplaintCategoryConverter implements AttributeConverter<ComplaintCategory, String> {

    @Override
    public String convertToDatabaseColumn(ComplaintCategory attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ComplaintCategory convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ComplaintCategory.fromValue(dbData);
    }
}
