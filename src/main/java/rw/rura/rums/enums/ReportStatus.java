package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportStatus {
    DRAFT("draft"),
    PUBLISHED("published"),
    ARCHIVED("archived");

    private final String value;

    ReportStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ReportStatus fromValue(String value) {
        for (ReportStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ReportStatus: " + value);
    }
}
