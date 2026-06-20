package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FraudIndicatorType {
    UNUSUAL_BILLING_PATTERN("Unusual Billing Pattern"),
    DUPLICATE_APPLICATIONS("Duplicate Applications"),
    IDENTITY_MISREPRESENTATION("Identity Misrepresentation"),
    REVENUE_UNDERREPORTING("Revenue Underreporting"),
    SPECTRUM_INTERFERENCE("Spectrum Interference"),
    UNLICENSED_OPERATION("Unlicensed Operation"),
    TARIFF_MANIPULATION("Tariff Manipulation"),
    METER_TAMPERING("Meter Tampering"),
    SIM_BOX_FRAUD("SIM Box Fraud"),
    GHOST_CUSTOMER_REGISTRATIONS("Ghost Customer Registrations");

    private final String value;

    FraudIndicatorType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static FraudIndicatorType fromValue(String value) {
        for (FraudIndicatorType item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown FraudIndicatorType: " + value);
    }
}
