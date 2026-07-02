package dev.vorstu.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateStudentRequest {
    @NotNull
    @Size(max = 64)
    String fio;
    @Pattern(regexp = "\\+?[0-9]{10,15}$")
    String phoneNumber;
}
