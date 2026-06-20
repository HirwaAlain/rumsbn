package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Province {
    KIGALI_CITY("Kigali City"),
    NORTHERN_PROVINCE("Northern Province"),
    SOUTHERN_PROVINCE("Southern Province"),
    EASTERN_PROVINCE("Eastern Province"),
    WESTERN_PROVINCE("Western Province");

    private final String value;

    Province(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static Province fromValue(String value) {
        for (Province item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown Province: " + value);
    }
}
