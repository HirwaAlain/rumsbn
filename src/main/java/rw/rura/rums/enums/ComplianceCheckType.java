package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ComplianceCheckType {
    ANNUAL_RETURN_FILING("Annual Return Filing"),
    QOS_AUDIT("Quality of Service (QoS) Audit"),
    UNIVERSAL_ACCESS_OBLIGATION("Universal Access Obligation"),
    SPECTRUM_USAGE_COMPLIANCE("Spectrum Usage Compliance"),
    TARIFF_FILING("Tariff Filing"),
    CONSUMER_PROTECTION_AUDIT("Consumer Protection Audit"),
    NETWORK_ROLLOUT_TARGET("Network Rollout Target"),
    ENVIRONMENTAL_COMPLIANCE("Environmental Compliance"),
    FINANCIAL_REPORTING("Financial Reporting"),
    SECURITY_AND_DATA_PROTECTION_AUDIT("Security & Data Protection Audit");

    private final String value;

    ComplianceCheckType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ComplianceCheckType fromValue(String value) {
        for (ComplianceCheckType item : values()) {
            if (item.value.equals(value)) return item;
        }
        throw new IllegalArgumentException("Unknown ComplianceCheckType: " + value);
    }
}
