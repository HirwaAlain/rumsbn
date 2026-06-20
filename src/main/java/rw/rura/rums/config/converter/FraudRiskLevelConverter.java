package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.FraudRiskLevel;

@Converter(autoApply = true)
public class FraudRiskLevelConverter implements AttributeConverter<FraudRiskLevel, String> {

    @Override
    public String convertToDatabaseColumn(FraudRiskLevel attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public FraudRiskLevel convertToEntityAttribute(String dbData) {
        return dbData == null ? null : FraudRiskLevel.fromValue(dbData);
    }
}
