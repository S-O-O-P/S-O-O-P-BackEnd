package com.soop.jwtsecurity.controller;

import com.soop.jwtsecurity.entityDTO.RefreshEntity;
import com.soop.jwtsecurity.jwt.JWTUtil;
import com.soop.jwtsecurity.mapper.UserMapper;
import io.jsonwebtoken.ExpiredJwtException;
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

@Controller
@ResponseBody
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final UserMapper userMapper;

    public ReissueController(JWTUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @PostMapping("/reissue")
    public void reissue(@RequestBody Map<String, Object> requestBody, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = null;
        Integer userCode = (Integer) requestBody.get("userCode");

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

        // 액세스 토큰이 없으면 에러 처리
        if (accessToken == null) {
            response.sendRedirect("http://localhost:3001/login?error=access_token_missing");
            return;
        }

        String signupPlatform = null;
        try {
            signupPlatform = jwtUtil.getSignupPlatformFromToken(accessToken);
        } catch (ExpiredJwtException e) {
            response.sendRedirect("http://localhost:3001/login?error=access_token_expired");
            return;
        } catch (Exception e) {
            response.sendRedirect("http://localhost:3001/login?error=invalid_access_token");
            return;
        }

        if (signupPlatform == null) {
            response.sendRedirect("http://localhost:3001/login?error=invalid_signup_platform");
            return;
        }

        // 서버 DB에서 해당 사용자의 리프레시 토큰 조회
        String refreshToken = userMapper.searchRefreshEntity(signupPlatform);
        if (refreshToken == null) {
            response.sendRedirect("http://localhost:3001/login?error=refresh_token_missing");
            return;
        }

        // 프로필 사진 정보 조회
        String profilePic = userMapper.getProfilePic(signupPlatform);

        // 리프레시 토큰 유효성 검사
        try {
            if (jwtUtil.isExpired(refreshToken)) {
                response.sendRedirect("http://localhost:3001/login?error=refresh_token_expired");
                return;
            }
        } catch (ExpiredJwtException e) {
            response.sendRedirect("http://localhost:3001/login?error=refresh_token_expired");
            return;
        } catch (Exception e) {
            response.sendRedirect("http://localhost:3001/login?error=invalid_refresh_token");
            return;
        }

        // 새로운 액세스 토큰 발급
        String role = jwtUtil.getRoleFromRefreshToken(refreshToken);
        String newAccessToken = jwtUtil.createJwt("access", signupPlatform, role, userCode, profilePic, 300L * 1000);


        createAndAddCookie(response, "access", newAccessToken);

        response.setStatus(HttpStatus.OK.value());
    }

    private void createAndAddCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(5 * 60); // 20 minutes
        cookie.setDomain("localhost");
        cookie.setHttpOnly(false); // JavaScript에서 접근 불가하도록 설정
        cookie.setPath("/");
        cookie.setSecure(false); // localhost 환경에서는 false, 실제 배포 시 true로 설정

        response.addCookie(cookie);
    }
}
