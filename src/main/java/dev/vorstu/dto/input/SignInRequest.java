package dev.vorstu.dto.input;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {
    @NotNull
    @Size(max = 70)
    private String login;
    @NotNull
    @Size(max = 70)
    private String password;
}