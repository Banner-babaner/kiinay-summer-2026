package dev.vorstu.service;

import dev.vorstu.entity.UserAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    private SecretKey signedKey;


    private SecretKey getSigningKey() {
        if(signedKey!=null) return signedKey;
        byte[] keyBytes = secret.getBytes();
        signedKey = Keys.hmacShaKeyFor(keyBytes);
        return signedKey;
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token){
        return extractClaim(token, (Claims)->Claims.get("type", String.class));
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");

        if (userDetails instanceof UserAuth) {
            claims.put("id", ((UserAuth) userDetails).getId());
        }

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey())
                .compact();
    }


    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        String login = extractUsername(token);
        if(!Objects.equals(extractTokenType(token), "access")){
            log.warn("Using refresh token instead access token by {}", login);
            return false;
        }
        return (login.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        String login = extractUsername(token);
        if(!Objects.equals(extractTokenType(token), "refresh")){
            log.warn("Using access token instead refresh token by {}", login);
            return false;
        }
        return (login.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
