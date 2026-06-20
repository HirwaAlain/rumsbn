package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.UserStatus;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, String> {

    @Override
    public String convertToDatabaseColumn(UserStatus attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public UserStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : UserStatus.fromValue(dbData);
    }
}
