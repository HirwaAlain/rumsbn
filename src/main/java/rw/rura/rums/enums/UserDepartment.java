package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserDepartment {
    EXECUTIVE("Executive"),
    LICENSING("Licensing"),
    COMPLIANCE("Compliance"),
    COMPLAINTS("Complaints"),
    FRAUD_AND_INVESTIGATIONS("Fraud & Investigations"),
    LEGAL("Legal"),
    ICT("ICT"),
    FINANCE("Finance"),
    HUMAN_RESOURCES("Human Resources"),
    COMMUNICATIONS("Communications");

    private final String value;

    UserDepartment(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static UserDepartment fromValue(String value) {
        for (UserDepartment item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown UserDepartment: " + value);
    }
}
