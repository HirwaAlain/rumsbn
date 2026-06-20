package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.ReportStatus;

@Converter(autoApply = true)
public class ReportStatusConverter implements AttributeConverter<ReportStatus, String> {

    @Override
    public String convertToDatabaseColumn(ReportStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ReportStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ReportStatus.fromValue(dbData);
    }
}
