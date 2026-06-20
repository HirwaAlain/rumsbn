package rw.rura.rums.config.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import rw.rura.rums.enums.UserDepartment;

@Converter(autoApply = true)
public class UserDepartmentConverter implements AttributeConverter<UserDepartment, String> {

    @Override
    public String convertToDatabaseColumn(UserDepartment attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public UserDepartment convertToEntityAttribute(String dbData) {
        return dbData == null ? null : UserDepartment.fromValue(dbData);
    }
}
