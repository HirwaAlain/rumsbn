package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportType {
    MONTHLY("monthly"),
    QUARTERLY("quarterly"),
    ANNUAL("annual"),
    AD_HOC("ad_hoc");

    private final String value;

    ReportType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ReportType fromValue(String value) {
        for (ReportType item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ReportType: " + value);
    }
}
