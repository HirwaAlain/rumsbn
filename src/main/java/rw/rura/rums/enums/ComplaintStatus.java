package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ComplaintStatus {
    OPEN("open"),
    UNDER_REVIEW("under_review"),
    RESOLVED("resolved"),
    CLOSED("closed"),
    ESCALATED("escalated");

    private final String value;

    ComplaintStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ComplaintStatus fromValue(String value) {
        for (ComplaintStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ComplaintStatus: " + value);
    }
}
