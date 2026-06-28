package dev.vorstu.dto.input;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {
    private String login;
    private String password;
}