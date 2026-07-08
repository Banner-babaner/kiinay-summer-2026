package dev.vorstu.service;

import dev.vorstu.dto.input.SignInRequest;
import dev.vorstu.dto.input.SignUpRequest;
import dev.vorstu.dto.output.AuthResponse;
import dev.vorstu.entity.Authable;
import dev.vorstu.entity.UserAuth;
import dev.vorstu.exception.auth.DuplicateLoginException;
import dev.vorstu.exception.auth.InvalidLoginFormatException;
import dev.vorstu.exception.auth.InvalidPasswordFormatException;
import dev.vorstu.exception.auth.InvalidTokenException;
import dev.vorstu.parser.CsvParser;
import dev.vorstu.repository.InviteApplicationRepository;
import dev.vorstu.repository.UserAuthRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserAuthRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(@NonNull SignInRequest request) {

        if(request.getLogin()==null || request.getLogin().length()>70){
            throw new InvalidLoginFormatException("Login must be a string containing 0-70 chars");
        }

        if(request.getPassword()==null || request.getPassword().length()>70){
            throw new InvalidPasswordFormatException("Login must be a string containing 0-70 chars");
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword())
        );

        UserAuth user = (UserAuth) auth.getPrincipal();


        return AuthResponse.builder()
                .accountId(user.getId())
                .login(request.getLogin())
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    public AuthResponse refreshAuth(@NonNull String refreshToken){
        if(jwtService.isTokenExpired(refreshToken))
            throw new InvalidTokenException("expired");
        String login =  jwtService.extractUsername(refreshToken);
        UserAuth user = repository.findByLogin(login)
                .orElseThrow(()->new UsernameNotFoundException(login));

        return AuthResponse.builder()
                .accountId(user.getId())
                .login(user.getLogin())
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }


    public AuthResponse register(@NonNull SignUpRequest request, @NonNull Authable person){
        UserAuth saved = registerAccount(request);
        person.setAuth(saved);
        return AuthResponse.builder()
                .accountId(saved.getId())
                .login(saved.getLogin())
                .accessToken(jwtService.generateAccessToken(saved))
                .refreshToken(jwtService.generateRefreshToken(saved))
                .build();
    }


    public AuthResponse register(SignUpRequest request) {
        UserAuth saved = registerAccount(request);
        return AuthResponse.builder()
                .accountId(saved.getId())
                .login(saved.getLogin())
                .accessToken(jwtService.generateAccessToken(saved))
                .refreshToken(jwtService.generateRefreshToken(saved))
                .build();
    }


    public void checkDuplicateLogins(List<String> logins){
        List<String> duplicates = repository.findLoginByLoginIn(logins);
        if(!duplicates.isEmpty())
            throw new DuplicateLoginException(duplicates);
    }

    private UserAuth registerAccount(SignUpRequest request){
        if(repository.existsByLogin(request.getLogin()))
            throw new DuplicateLoginException(request.getLogin());
        UserAuth auth = UserAuth.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        return repository.save(auth);
    }


}
