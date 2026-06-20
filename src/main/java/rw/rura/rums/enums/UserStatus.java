package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    SUSPENDED("suspended");

    private final String value;

    UserStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static UserStatus fromValue(String value) {
        for (UserStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown UserStatus: " + value);
    }
}
