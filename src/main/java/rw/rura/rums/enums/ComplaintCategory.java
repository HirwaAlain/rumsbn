package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ComplaintCategory {
    BILLING_DISPUTE("Billing Dispute"),
    SERVICE_INTERRUPTION("Service Interruption"),
    POOR_QUALITY_OF_SERVICE("Poor Quality of Service"),
    UNAUTHORIZED_CHARGES("Unauthorized Charges"),
    CONTRACT_VIOLATION("Contract Violation"),
    CUSTOMER_SERVICE_FAILURE("Customer Service Failure"),
    DATA_PRIVACY_BREACH("Data Privacy Breach"),
    TARIFF_OVERCHARGE("Tariff Overcharge"),
    CONNECTION_DELAY("Connection Delay"),
    OTHER("Other");

    private final String value;

    ComplaintCategory(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ComplaintCategory fromValue(String value) {
        for (ComplaintCategory item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ComplaintCategory: " + value);
    }
}
