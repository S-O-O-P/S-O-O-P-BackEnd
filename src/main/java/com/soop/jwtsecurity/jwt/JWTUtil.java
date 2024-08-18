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

@Tag(name = "JWT 유틸 설정", description = "secret 설정")
@Component
public class JWTUtil {

    // 시크릿키를 저장하는 필드
    private final SecretKey secretKey;

    // 생성자를 통해 시크릿 키를 설정
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 토큰에서 클레임을 추출하는 메서드
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 가입 플랫폼 정보를 추출하는 메서드
    public String getSignupPlatform(String token) {
        return getClaims(token).get("signupPlatform", String.class);
    }

    // 토큰에서 사용자 역할 정보를 추출하는 메서드
    public String getUserRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 토큰의 만료 여부를 확인하는 메서드
    public Boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // 토큰에서 카테고리 정보를 추출하는 메서드
    public String getCategory(String token) {
        return getClaims(token).get("category", String.class);
    }

    // JWT 토큰을 생성하는 메서드
    public String createJwt(String category, String signupPlatform, String role, int userCode, String profilePic, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category)
                .claim("signupPlatform", signupPlatform)
                .claim("role", role)
                .claim("userCode", userCode)
                .claim("profilePic", profilePic)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // 리프레시 토큰이 유효한지 검증하는 메서드
    public boolean validateRefreshToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 리프레시 토큰에서 사용자 이름을 추출하는 메서드
    public String getUsernameFromRefreshToken(String token) {
        return getClaims(token).getSubject();
    }

    // 리프레시 토큰에서 사용자 역할 정보를 추출하는 메서드
    public String getRoleFromRefreshToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 새로운 액세스 토큰을 생성하는 메서드
    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("category", "access")
                .setExpiration(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10분 유효기간
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 가입 플랫폼 정보를 추출하는 메서드
    public String getSignupPlatformFromToken(String token) {
        return getClaims(token).get("signupPlatform", String.class);
    }
}
