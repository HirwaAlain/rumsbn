package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClmsCaseType {
    NEW_LICENSE("new_license"),
    LICENSE_RENEWAL("license_renewal"),
    LICENSE_AMENDMENT("license_amendment"),
    LICENSE_REVOCATION("license_revocation"),
    TARIFF_REVIEW("tariff_review"),
    SPECTRUM_ASSIGNMENT("spectrum_assignment"),
    TYPE_APPROVAL("type_approval"),
    DISPUTE_RESOLUTION("dispute_resolution");

    private final String value;

    ClmsCaseType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ClmsCaseType fromValue(String value) {
        for (ClmsCaseType item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ClmsCaseType: " + value);
    }
}
