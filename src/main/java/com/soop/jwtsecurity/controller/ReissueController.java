package com.soop.jwtsecurity.controller;

import com.soop.jwtsecurity.jwt.JWTUtil;
import com.soop.jwtsecurity.mapper.UserMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Map;

@Tag(name="토큰 재발급 컨트롤러")
@Controller
@ResponseBody
public class ReissueController {

    // JWT 토큰을 처리하는 유틸리티 클래스
    private final JWTUtil jwtUtil;

    // 데이터베이스에서 사용자 정보를 조회하는 Mapper 인터페이스
    private final UserMapper userMapper;

    // 생성자를 통해 JWTUtil 및 UserMapper 객체를 주입
    public ReissueController(JWTUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil; // JWTUtil 주입
        this.userMapper = userMapper; // UserMapper 주입
    }

    @Operation(summary = "토큰 재발급 REST API", description = "클라이언트 에게서 access토큰을 확인 DB에 저장된 refresh 토큰과 비교하여 재발급")
    @PostMapping("/reissue")
    public void reissue(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = null; // accessToken 을 빈 문자열로 초기화
        Integer userCode = (Integer) requestBody.get("userCode"); // "userCode" 를 Integer 타입으로 변환

        // 쿠키에서 액세스 토큰 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        // 액세스 토큰이 없을 경우 로그인 페이지로 리다이렉트
        if (accessToken == null) {
            response.sendRedirect("http://localhost:3001/login?error=access_token_missing");
            return;
        }
        String signupPlatform = null; // 토큰에서 회원가입 플랫폼을 추출할 변수 초기화
        try {
            signupPlatform = jwtUtil.getSignupPlatformFromToken(accessToken); // 토큰에서 회원가입 플랫폼 정보 추출
        } catch (ExpiredJwtException e) { // 토큰이 만료된 경우 처리
            response.sendRedirect("http://localhost:3001/login?error=access_token_expired");
            return;
        } catch (Exception e) { // 그 외의 예외 처리
            response.sendRedirect("http://localhost:3001/login?error=invalid_access_token");
            return;
        }

        // 회원가입 플랫폼 정보가 없는 경우 처리
        if (signupPlatform == null) {
            response.sendRedirect("http://localhost:3001/login?error=invalid_signup_platform");
            return;
        }

        // DB에서 회원가입 플랫폼에 해당하는 리프레시 토큰을 조회
        String refreshToken = userMapper.searchRefreshEntity(signupPlatform);
        if (refreshToken == null) { // 리프레시 토큰이 없을 경우 처리
            response.sendRedirect("http://localhost:3001/login?error=refresh_token_missing");
            return;
        }

        // DB에서 회원가입 플랫폼에 해당하는 프로필 사진 정보를 조회
        String profilePic = userMapper.getProfilePic(signupPlatform);

        // 리프레시 토큰의 유효성을 검사
        try {
            if (jwtUtil.isExpired(refreshToken)) { // 리프레시 토큰이 만료된 경우 처리
                response.sendRedirect("http://localhost:3001/login?error=refresh_token_expired");
                return;
            }
        } catch (ExpiredJwtException e) { // 리프레시 토큰이 만료된 경우 예외 처리
            response.sendRedirect("http://localhost:3001/login?error=refresh_token_expired");
            return;
        } catch (Exception e) { // 그 외의 예외 처리
            response.sendRedirect("http://localhost:3001/login?error=invalid_refresh_token");
            return;
        }

        // 유효한 리프레시 토큰을 사용하여 새로운 액세스 토큰을 생성
        String role = jwtUtil.getRoleFromRefreshToken(refreshToken); // 리프레시 토큰에서 사용자 역할 추출
        String newAccessToken = jwtUtil.createJwt("access", signupPlatform, role, userCode, profilePic, 300L * 1000); // 새로운 액세스 토큰 생성

        // 생성된 액세스 토큰을 쿠키에 저장하여 클라이언트에 전달
        createAndAddCookie(response, "access", newAccessToken);

        response.setStatus(HttpStatus.OK.value()); // HTTP 응답 상태를 200 OK로 설정
    }

    @Operation(summary = "쿠키 생성 메소드")
    private void createAndAddCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = new Cookie(key, value); // 새로운 쿠키 객체 생성
        cookie.setMaxAge(5 * 60); // 쿠키 유효기간 설정 (5분)
        cookie.setDomain("localhost"); // 쿠키의 도메인 설정
        cookie.setHttpOnly(false); // JavaScript에서 접근 가능하도록 설정
        cookie.setPath("/"); // 쿠키의 유효 경로 설정
        cookie.setSecure(false); // HTTPS에서만 쿠키가 전송되도록 설정 (실제 배포 시 true로 설정)

        response.addCookie(cookie); // 생성된 쿠키를 응답에 추가
    }
}
