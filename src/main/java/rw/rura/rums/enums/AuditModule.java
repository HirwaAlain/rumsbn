package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuditModule {
    LICENSES("Licenses"),
    COMPLAINTS("Complaints"),
    COMPLIANCE("Compliance"),
    FRAUD("Fraud"),
    REPORTS("Reports"),
    USERS("Users"),
    WORKFLOWS("Workflows"),
    ALERTS("Alerts"),
    CLMS("CLMS"),
    SYSTEM("System");

    private final String value;

    AuditModule(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static AuditModule fromValue(String value) {
        for (AuditModule item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown AuditModule: " + value);
    }
}
