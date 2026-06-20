package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Sector {
    TELECOM("Telecom"),
    ENERGY("Energy"),
    WATER("Water"),
    TRANSPORT("Transport");

    private final String value;

    Sector(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static Sector fromValue(String value) {
        for (Sector item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown Sector: " + value);
    }
}
