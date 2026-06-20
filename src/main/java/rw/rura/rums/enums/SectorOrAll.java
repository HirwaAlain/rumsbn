package rw.rura.rums.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * Represents a sector filter that accepts any {@link Sector} value or the
 * literal "All Sectors". Used by Workflow.sector and Report.sector, which are
 * stored as VARCHAR in the database (not a PostgreSQL enum) to allow this
 * extended value set.
 */
public final class SectorOrAll {

    public static final String ALL_SECTORS_VALUE = "All Sectors";

    public static final SectorOrAll ALL = new SectorOrAll(ALL_SECTORS_VALUE);
    public static final SectorOrAll TELECOM   = new SectorOrAll(Sector.TELECOM.getValue());
    public static final SectorOrAll ENERGY    = new SectorOrAll(Sector.ENERGY.getValue());
    public static final SectorOrAll WATER     = new SectorOrAll(Sector.WATER.getValue());
    public static final SectorOrAll TRANSPORT = new SectorOrAll(Sector.TRANSPORT.getValue());

    private final String value;

    private SectorOrAll(String value) {
        this.value = value;
    }

    /**
     * Factory used by Jackson for JSON deserialisation and by application code.
     * Accepts any valid {@link Sector} display value or exactly "All Sectors".
     */
    @JsonCreator
    public static SectorOrAll fromString(String value) {
        if (ALL_SECTORS_VALUE.equals(value)) {
            return ALL;
        }
        // Validate against the Sector enum — throws if unrecognised
        Sector sector = Sector.fromValue(value);
        return new SectorOrAll(sector.getValue());
    }

    /** Factory for converting a known {@link Sector} to SectorOrAll. */
    public static SectorOrAll of(Sector sector) {
        return new SectorOrAll(sector.getValue());
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public boolean isAllSectors() {
        return ALL_SECTORS_VALUE.equals(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectorOrAll other)) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
