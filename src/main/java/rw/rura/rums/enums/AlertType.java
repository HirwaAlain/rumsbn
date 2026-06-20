package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AlertType {
    LICENSE_EXPIRY("license_expiry"),
    COMPLIANCE_BREACH("compliance_breach"),
    FRAUD_DETECTED("fraud_detected"),
    COMPLAINT_SLA_BREACH("complaint_sla_breach"),
    WORKFLOW_STALLED("workflow_stalled"),
    SYSTEM_ERROR("system_error"),
    REPORT_READY("report_ready"),
    USER_SUSPENDED("user_suspended"),
    THRESHOLD_EXCEEDED("threshold_exceeded");

    private final String value;

    AlertType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static AlertType fromValue(String value) {
        for (AlertType item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown AlertType: " + value);
    }
}
