package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClmsCaseStatus {
    DRAFT("draft"),
    SUBMITTED("submitted"),
    UNDER_REVIEW("under_review"),
    APPROVED("approved"),
    REJECTED("rejected"),
    APPEALED("appealed"),
    CLOSED("closed");

    private final String value;

    ClmsCaseStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ClmsCaseStatus fromValue(String value) {
        for (ClmsCaseStatus item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ClmsCaseStatus: " + value);
    }
}
