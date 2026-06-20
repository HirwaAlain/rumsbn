package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkflowTrigger {
    LICENSE_APPLICATION("license_application"),
    COMPLAINT_FILED("complaint_filed"),
    COMPLIANCE_DUE("compliance_due"),
    FRAUD_ALERT("fraud_alert"),
    RENEWAL_DUE("renewal_due"),
    MANUAL("manual");

    private final String value;

    WorkflowTrigger(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static WorkflowTrigger fromValue(String value) {
        for (WorkflowTrigger item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown WorkflowTrigger: " + value);
    }
}
