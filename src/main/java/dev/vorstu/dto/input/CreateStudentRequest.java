package dev.vorstu.dto.input;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateStudentRequest {
    @NotNull
    @Size(max = 64)
    String fio;
    @Pattern(regexp = "\\+?[0-9]{10,15}$")
    String phoneNumber;
    @Email
    @Size(max = 255)
    String email;
}
