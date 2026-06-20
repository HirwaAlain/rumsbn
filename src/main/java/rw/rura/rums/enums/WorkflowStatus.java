package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkflowStatus {
    DRAFT("draft"),
    ACTIVE("active"),
    PAUSED("paused"),
    COMPLETED("completed"),
    FAILED("failed");

    private final String value;

    WorkflowStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static WorkflowStatus fromValue(String value) {
        for (WorkflowStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown WorkflowStatus: " + value);
    }
}
