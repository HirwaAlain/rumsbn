package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LicenseCategory {
    MOBILE_NETWORK_OPERATOR("Mobile Network Operator"),
    FIXED_NETWORK_OPERATOR("Fixed Network Operator"),
    INTERNET_SERVICE_PROVIDER("Internet Service Provider"),
    PUBLIC_SWITCHED_TELEPHONE_NETWORK("Public Switched Telephone Network"),
    VIRTUAL_NETWORK_OPERATOR("Virtual Network Operator"),
    SPECTRUM_LICENSE("Spectrum License"),
    ELECTRICITY_DISTRIBUTION("Electricity Distribution"),
    ELECTRICITY_TRANSMISSION("Electricity Transmission"),
    POWER_GENERATION("Power Generation"),
    WATER_SUPPLY("Water Supply"),
    SANITATION_SERVICES("Sanitation Services"),
    ROAD_TRANSPORT_OPERATOR("Road Transport Operator"),
    FREIGHT_AND_LOGISTICS("Freight & Logistics"),
    BROADCASTING("Broadcasting");

    private final String value;

    LicenseCategory(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static LicenseCategory fromValue(String value) {
        for (LicenseCategory item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown LicenseCategory: " + value);
    }
}
