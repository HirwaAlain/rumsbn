package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.Sector;

@Converter(autoApply = true)
public class SectorConverter implements AttributeConverter<Sector, String> {

    @Override
    public String convertToDatabaseColumn(Sector attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Sector convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Sector.fromValue(dbData);
    }
}
