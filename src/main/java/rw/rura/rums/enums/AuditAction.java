package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AuditAction {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    APPROVE("approve"),
    REJECT("reject"),
    SUSPEND("suspend"),
    REINSTATE("reinstate"),
    EXPORT("export"),
    LOGIN("login"),
    LOGOUT("logout"),
    PASSWORD_RESET("password_reset"),
    PERMISSION_CHANGE("permission_change");

    private final String value;

    AuditAction(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static AuditAction fromValue(String value) {
        for (AuditAction item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown AuditAction: " + value);
    }
}
