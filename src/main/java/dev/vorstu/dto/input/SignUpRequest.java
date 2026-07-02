package dev.vorstu.dto.input;

import dev.vorstu.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @NotNull
    @Size(max = 70)
    private String login;
    @NotNull
    @Size(max = 70)
    private String password;
    @NotNull
    @Size(max = 20)
    private UserRole role;
}
