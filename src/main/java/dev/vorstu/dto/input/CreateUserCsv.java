package dev.vorstu.dto.input;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import dev.vorstu.converter.CsvUserRoleConverter;
import dev.vorstu.entity.UserRole;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserCsv {
    @CsvBindByName(column = "FIO", required = true)
    private String fio;
    @CsvCustomBindByName(column = "ROLE", required = true, converter = CsvUserRoleConverter.class)
    private UserRole role;
    @CsvBindByName(column = "EMAIL", required = true)
    private String email;
    @CsvBindByName(column = "GROUP_NAME")
    private String groupName;
    @CsvBindByName(column = "PHONE_NUMBER")
    private String phoneNumber;
    @CsvBindByName(column = "LOGIN")
    String login;
    @CsvBindByName(column = "PASSWORD")
    String password;
}
