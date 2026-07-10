package dev.vorstu.controller.common;

import dev.vorstu.dto.input.SignInRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.service.AuthService;
import dev.vorstu.service.InviteApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final InviteApplicationService inviteApplicationService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @NotNull String refreshToken){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.refreshAuth(refreshToken));
    }

    @GetMapping("/invites/{secretKey}")
    public ResponseEntity<Void> useInviteLink(@PathVariable("secretKey") String secretKey){
        inviteApplicationService.useSecretKey(secretKey);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
