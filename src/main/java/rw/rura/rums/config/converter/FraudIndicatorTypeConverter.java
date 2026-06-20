package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.FraudIndicatorType;

@Converter(autoApply = true)
public class FraudIndicatorTypeConverter implements AttributeConverter<FraudIndicatorType, String> {

    @Override
    public String convertToDatabaseColumn(FraudIndicatorType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public FraudIndicatorType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : FraudIndicatorType.fromValue(dbData);
    }
}
