package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FraudCaseStatus {
    OPEN("open"),
    INVESTIGATING("investigating"),
    CONFIRMED("confirmed"),
    DISMISSED("dismissed"),
    REFERRED("referred");

    private final String value;

    FraudCaseStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static FraudCaseStatus fromValue(String value) {
        for (FraudCaseStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown FraudCaseStatus: " + value);
    }
}
