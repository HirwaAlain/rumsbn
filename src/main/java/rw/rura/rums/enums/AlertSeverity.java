package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AlertSeverity {
    INFO("info"),
    WARNING("warning"),
    CRITICAL("critical");

    private final String value;

    AlertSeverity(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static AlertSeverity fromValue(String value) {
        for (AlertSeverity item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown AlertSeverity: " + value);
    }
}
