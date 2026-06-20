package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AlertStatus {
    UNREAD("unread"),
    READ("read"),
    DISMISSED("dismissed"),
    ACTIONED("actioned");

    private final String value;

    AlertStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static AlertStatus fromValue(String value) {
        for (AlertStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown AlertStatus: " + value);
    }
}
