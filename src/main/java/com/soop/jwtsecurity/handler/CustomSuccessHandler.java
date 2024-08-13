package com.soop.jwtsecurity.handler;

import com.soop.jwtsecurity.dto.CustomOAuth2User;
import com.soop.jwtsecurity.entityDTO.RefreshEntity;
import com.soop.jwtsecurity.entityDTO.UserEntity;
import com.soop.jwtsecurity.jwt.JWTUtil;
import com.soop.jwtsecurity.mapper.UserMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Tag(name = "커스텀 로그인 성공 핸들러", description = "SimpleUrlAuthenticationSuccessHandler 커스텀")
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // JWT 토큰 생성 및 검증 유틸리티 클래스
    private final JWTUtil jwtUtil;

    // 사용자 정보를 조회하는 Mapper 인터페이스
    private final UserMapper userMapper;

    // 생성자를 통해 JWTUtil 및 UserMapper 객체를 주입
    public CustomSuccessHandler(JWTUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil; // JWTUtil 주입
        this.userMapper = userMapper; // UserMapper 주입
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 인증된 사용자의 CustomOAuth2User 객체를 가져옴
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        // 사용자 이름을 가져옴
        String username = customUserDetails.getUsername();

        // 사용자 정보를 데이터베이스에서 조회
        int userCode = userMapper.findBySignupPlatform(username).getUserCode();
        String profilePic = userMapper.findBySignupPlatform(username).getProfilePic();
        String gender = userMapper.findBySignupPlatform(username).getGender();

        // 사용자의 권한 정보를 가져옴
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 새로운 액세스 토큰을 생성 (유효 기간 5분)
        String access = jwtUtil.createJwt("access", username, role, userCode, profilePic, 300L * 1000); // 5분 (300초)

        // 기존 리프레시 토큰을 삭제
        String existingRefreshToken = userMapper.searchRefreshEntity(username);
        if (existingRefreshToken != null) {
            userMapper.deleteByRefresh(existingRefreshToken);
        }

        // 새로운 리프레시 토큰을 생성 (유효 기간 24시간)
        String refresh = jwtUtil.createJwt("refresh", username, role, userCode, profilePic, 86400L * 1000); // 24시간 (86400초)
        addRefreshEntity(username, refresh, 86400L * 1000);

        // 생성된 액세스 토큰을 쿠키에 저장
        createAndAddCookie(response, "access", access);

        // 사용자 가입 상태에 따라 리다이렉트
        UserEntity userEntity = new UserEntity();
        if (userMapper.findAboutMe(username) == null) {
            // 가입 후 정보 입력이 필요한 경우
            response.sendRedirect("http://localhost:3001/signup");
        } else {
            // 가입 후 메인 페이지로 리다이렉트
            response.sendRedirect("http://localhost:3001/main");
        }
    }

    // 리프레시 토큰을 데이터베이스에 저장
    private void addRefreshEntity(String signupPlatform, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setSignupPlatform(signupPlatform);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());
        userMapper.saveRefreshEntity(refreshEntity);
    }

    // 쿠키를 생성하고 응답에 추가
    private void createAndAddCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(5 * 60); // 쿠키 유효 기간 설정 (5분)
        cookie.setDomain("localhost"); // 쿠키의 도메인 설정
        cookie.setHttpOnly(false); // JavaScript에서 접근 가능하도록 설정
        cookie.setPath("/"); // 쿠키의 유효 경로 설정
        cookie.setSecure(true); // HTTPS에서만 쿠키가 전송되도록 설정 (로컬 환경에서는 false, 배포 시 true로 설정)
        response.addCookie(cookie);

        // SameSite 설정 추가 (필요시 주석 해제)
        // response.setHeader("Set-Cookie", String.format("%s=%s; Max-Age=%d; Domain=%s; Path=%s; HttpOnly; SameSite=None; Secure", key, value, 10 * 60, "localhost", "/"));
    }
}
