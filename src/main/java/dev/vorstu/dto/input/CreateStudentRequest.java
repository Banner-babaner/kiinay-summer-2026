package dev.vorstu.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateStudentRequest {
    @NotBlank
    @Size(max = 64)
    String fio;
    @Size(max = 64)
    Long groupId;
    @Size(max = 24)
    String phoneNumber;
}
