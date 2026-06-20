package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.WorkflowStatus;

@Converter(autoApply = true)
public class WorkflowStatusConverter implements AttributeConverter<WorkflowStatus, String> {

    @Override
    public String convertToDatabaseColumn(WorkflowStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public WorkflowStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : WorkflowStatus.fromValue(dbData);
    }
}
