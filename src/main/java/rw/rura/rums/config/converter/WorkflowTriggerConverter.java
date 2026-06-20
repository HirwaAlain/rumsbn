package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.WorkflowTrigger;

@Converter(autoApply = true)
public class WorkflowTriggerConverter implements AttributeConverter<WorkflowTrigger, String> {

    @Override
    public String convertToDatabaseColumn(WorkflowTrigger attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public WorkflowTrigger convertToEntityAttribute(String dbData) {
        return dbData == null ? null : WorkflowTrigger.fromValue(dbData);
    }
}
