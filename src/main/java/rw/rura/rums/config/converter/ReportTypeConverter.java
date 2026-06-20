package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ReportType;

@Converter(autoApply = true)
public class ReportTypeConverter implements AttributeConverter<ReportType, String> {

    @Override
    public String convertToDatabaseColumn(ReportType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ReportType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ReportType.fromValue(dbData);
    }
}
