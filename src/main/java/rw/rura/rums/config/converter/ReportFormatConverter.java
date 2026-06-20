package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ReportFormat;

@Converter(autoApply = true)
public class ReportFormatConverter implements AttributeConverter<ReportFormat, String> {

    @Override
    public String convertToDatabaseColumn(ReportFormat attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ReportFormat convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ReportFormat.fromValue(dbData);
    }
}
