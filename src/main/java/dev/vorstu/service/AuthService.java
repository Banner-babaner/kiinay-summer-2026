package dev.vorstu.service;

import dev.vorstu.dto.input.SignInRequest;
import dev.vorstu.dto.output.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login(SignInRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        UserDetails user = (UserDetails) auth.getPrincipal();

        return AuthResponse.builder()
                .login(request.getLogin())
                .accessToken(jwtService.generateToken(user))
                .build();
    }
}
