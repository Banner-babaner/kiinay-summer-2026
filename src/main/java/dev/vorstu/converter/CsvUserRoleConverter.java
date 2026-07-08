package dev.vorstu.converter;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import dev.vorstu.entity.UserRole;
import lombok.NonNull;

public class CsvUserRoleConverter extends AbstractBeanField<UserRole, String> {
    @Override
    protected Object convert(@NonNull String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        s = s.trim().toUpperCase();
        try {
            return UserRole.valueOf(s);
        }
        catch (Exception e){
            throw new CsvDataTypeMismatchException("Excepted role "+s+" in enum UserRole");
        }
    }
}
