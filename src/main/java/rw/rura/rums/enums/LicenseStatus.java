package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LicenseStatus {
    ACTIVE("active"),
    PENDING("pending"),
    SUSPENDED("suspended"),
    REVOKED("revoked"),
    EXPIRED("expired"),
    REJECTED("rejected");

    private final String value;

    LicenseStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static LicenseStatus fromValue(String value) {
        for (LicenseStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown LicenseStatus: " + value);
    }
}
