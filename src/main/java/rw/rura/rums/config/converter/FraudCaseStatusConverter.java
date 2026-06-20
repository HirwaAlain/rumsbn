package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.FraudCaseStatus;

@Converter(autoApply = true)
public class FraudCaseStatusConverter implements AttributeConverter<FraudCaseStatus, String> {

    @Override
    public String convertToDatabaseColumn(FraudCaseStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public FraudCaseStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : FraudCaseStatus.fromValue(dbData);
    }
}
