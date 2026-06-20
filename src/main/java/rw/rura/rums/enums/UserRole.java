package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN("admin"),
    ANALYST("analyst"),
    AUDITOR("auditor"),
    SUPERVISOR("supervisor"),
    VIEWER("viewer");

    private final String value;

    UserRole(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static UserRole fromValue(String value) {
        for (UserRole item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown UserRole: " + value);
    }
}
