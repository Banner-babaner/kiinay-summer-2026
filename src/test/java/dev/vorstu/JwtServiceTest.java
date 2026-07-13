package dev.vorstu;

import dev.vorstu.entity.UserAuth;
import dev.vorstu.entity.UserRole;
import dev.vorstu.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "accessExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 86400000L);
    }

    @Test
    void extractUsernameShouldReturnLoginFromToken() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");
        userAuth.setId(1L);

        String token = jwtService.generateAccessToken(userAuth);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testUser");
    }

    @Test
    void extractTokenTypeShouldReturnAccessFromAccessToken() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateAccessToken(userAuth);

        String tokenType = jwtService.extractTokenType(token);

        assertThat(tokenType).isEqualTo("access");
    }

    @Test
    void extractTokenTypeShouldReturnRefreshFromRefreshToken() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateRefreshToken(userAuth);

        String tokenType = jwtService.extractTokenType(token);

        assertThat(tokenType).isEqualTo("refresh");
    }

    @Test
    void generateAccessTokenShouldCreateValidToken() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");
        userAuth.setId(1L);

        String token = jwtService.generateAccessToken(userAuth);

        assertThat(token).isNotNull();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testUser");
        assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
        assertThat((Long)jwtService.extractClaim(token, claims -> claims.get("id", Long.class))).isEqualTo(1L);
    }

    @Test
    void generateRefreshTokenShouldCreateValidToken() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateRefreshToken(userAuth);

        assertThat(token).isNotNull();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testUser");
        assertThat(jwtService.extractTokenType(token)).isEqualTo("refresh");
    }

    @Test
    void isAccessTokenValidShouldReturnTrueForValidAccessToken() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateAccessToken(userAuth);

        boolean isValid = jwtService.isAccessTokenValid(token, userAuth);

        assertThat(isValid).isTrue();
    }

    @Test
    void isAccessTokenValidShouldReturnFalseWhenTokenExpired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "accessExpiration", 1L);

        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateAccessToken(userAuth);

        Thread.sleep(2);

        assertThatThrownBy(
                ()->jwtService.isAccessTokenValid(token, userAuth))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void isAccessTokenValidShouldThrowExceptionWhenUsernameMismatch() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        UserAuth otherUser = new UserAuth();
        otherUser.setLogin("otherUser");

        String token = jwtService.generateAccessToken(userAuth);

        boolean isValid = jwtService.isAccessTokenValid(token, otherUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void isAccessTokenValidShouldReturnFalseWhenRefreshTokenUsed() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateRefreshToken(userAuth);

        boolean isValid = jwtService.isAccessTokenValid(token, userAuth);

        assertThat(isValid).isFalse();
    }

    @Test
    void isRefreshTokenValidShouldReturnTrueForValidRefreshToken() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateRefreshToken(userAuth);

        boolean isValid = jwtService.isRefreshTokenValid(token, userAuth);

        assertThat(isValid).isTrue();
    }

    @Test
    void isRefreshTokenValidShouldThrowExceptionWhenTokenExpired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 1L);

        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateRefreshToken(userAuth);

        Thread.sleep(2);

        assertThatThrownBy(
                ()->jwtService.isRefreshTokenValid(token, userAuth))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void isRefreshTokenValidShouldReturnFalseWhenUsernameMismatch() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        UserAuth otherUser = new UserAuth();
        otherUser.setLogin("otherUser");

        String token = jwtService.generateRefreshToken(userAuth);

        boolean isValid = jwtService.isRefreshTokenValid(token, otherUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void isRefreshTokenValidShouldReturnFalseWhenAccessTokenUsed() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateAccessToken(userAuth);

        boolean isValid = jwtService.isRefreshTokenValid(token, userAuth);

        assertThat(isValid).isFalse();
    }

    @Test
    void isTokenExpiredShouldReturnFalseForValidToken() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateAccessToken(userAuth);

        boolean isExpired = jwtService.isTokenExpired(token);

        assertThat(isExpired).isFalse();
    }

    @Test
    void isTokenExpiredShouldThrowExceptionForExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "accessExpiration", 1L);

        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateAccessToken(userAuth);

        Thread.sleep(2);

        assertThatThrownBy(
                ()->jwtService.isAccessTokenValid(token, userAuth))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void extractClaimShouldReturnSpecificClaim() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");
        userAuth.setId(1L);

        String token = jwtService.generateAccessToken(userAuth);

        Long id = jwtService.extractClaim(token, claims -> claims.get("id", Long.class));

        assertThat(id).isEqualTo(1L);
    }

    @Test
    void extractClaimShouldReturnSubject() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateAccessToken(userAuth);

        String subject = jwtService.extractClaim(token, Claims::getSubject);

        assertThat(subject).isEqualTo("testUser");
    }

    @Test
    void extractClaimShouldReturnExpiration() {
        UserAuth userAuth = new UserAuth();
        userAuth.setLogin("testUser");

        String token = jwtService.generateAccessToken(userAuth);

        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void getSigningKeyShouldReturnSameKey() {
        String token1 = jwtService.generateAccessToken(UserAuth.builder()
                .login("testUser")
                .password("pass")
                .role(UserRole.STUDENT)
                .build());
        String token2 = jwtService.generateAccessToken(UserAuth.builder()
                .login("testUser")
                .password("pass")
                .role(UserRole.STUDENT)
                .build());

        assertThat(token1).isEqualTo(token2);
    }
}