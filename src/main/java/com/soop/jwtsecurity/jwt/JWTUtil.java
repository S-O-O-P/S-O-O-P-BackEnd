package com.soop.jwtsecurity.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Tag(name = "JWT 유틸 설정",description = "secret 설정")
@Component
public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getSignupPlatform(String token) {
        return getClaims(token).get("signupPlatform", String.class);
    }

    public String getUserRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    public String createJwt(String category, String signupPlatform, String role, int userCode, String profilePic , Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("signupPlatform", signupPlatform)
                .claim("role", role)
                .claim("userCode",userCode)
                .claim("profilePic", profilePic)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateRefreshToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromRefreshToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getRoleFromRefreshToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("category", "access")
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10분 유효기간
                .signWith(secretKey)
                .compact();
    }

    public String getSignupPlatformFromToken(String token) {
        return getClaims(token).get("signupPlatform", String.class);
    }
}