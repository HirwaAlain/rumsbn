package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.WorkflowStepStatus;

@Converter(autoApply = true)
public class WorkflowStepStatusConverter implements AttributeConverter<WorkflowStepStatus, String> {

    @Override
    public String convertToDatabaseColumn(WorkflowStepStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public WorkflowStepStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : WorkflowStepStatus.fromValue(dbData);
    }
}
