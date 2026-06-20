package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkflowStepStatus {
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    SKIPPED("skipped"),
    FAILED("failed");

    private final String value;

    WorkflowStepStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static WorkflowStepStatus fromValue(String value) {
        for (WorkflowStepStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown WorkflowStepStatus: " + value);
    }
}
