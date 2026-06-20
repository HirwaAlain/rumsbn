package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ClmsCaseStatus;

@Converter(autoApply = true)
public class ClmsCaseStatusConverter implements AttributeConverter<ClmsCaseStatus, String> {

    @Override
    public String convertToDatabaseColumn(ClmsCaseStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ClmsCaseStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ClmsCaseStatus.fromValue(dbData);
    }
}
