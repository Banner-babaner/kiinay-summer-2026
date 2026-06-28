package dev.vorstu.controller;

import dev.vorstu.dto.input.SignInRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(SignInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.login(request));
    }
}
