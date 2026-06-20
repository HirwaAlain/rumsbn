package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ComplianceStatus {
    COMPLIANT("compliant"),
    NON_COMPLIANT("non_compliant"),
    UNDER_REVIEW("under_review"),
    REMEDIATION("remediation");

    private final String value;

    ComplianceStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ComplianceStatus fromValue(String value) {
        for (ComplianceStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ComplianceStatus: " + value);
    }
}
