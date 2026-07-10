package dev.vorstu.dto.input;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTeacherRequest {
    @NotBlank
    @Size(max = 64)
    String fio;
    @Pattern(regexp = "\\+?[0-9]{10,15}$")
    String phoneNumber;
    @Email
    @Size(max = 255)
    String email;
}
