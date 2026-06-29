package dev.vorstu.service;

import dev.vorstu.dto.input.SignInRequest;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.entity.UserAuth;
import dev.vorstu.exception.auth.DuplicateLoginException;
import dev.vorstu.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAuthRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(SignInRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword())
        );

        UserAuth user = (UserAuth) auth.getPrincipal();

        System.out.println(user.getUsername());
        System.out.println(user.getAuthorities());
        System.out.println(user.getRole());

        return AuthResponse.builder()
                .accountId(user.getId())
                .login(request.getLogin())
                .accessToken(jwtService.generateToken(user))
                .build();
    }


    public AuthResponse register(SignUpRequest request) {
        if(repository.existsByLogin(request.getLogin()))
            throw new DuplicateLoginException(request.getLogin());
        UserAuth auth = UserAuth.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        UserAuth saved = repository.save(auth);


        return AuthResponse.builder()
                .accountId(saved.getId())
                .login(saved.getLogin())
                .accessToken(jwtService.generateToken(saved))
                .build();
    }
}
