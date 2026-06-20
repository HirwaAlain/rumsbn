package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportFormat {
    PDF("pdf"),
    XLSX("xlsx"),
    CSV("csv");

    private final String value;

    ReportFormat(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ReportFormat fromValue(String value) {
        for (ReportFormat item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ReportFormat: " + value);
    }
}
