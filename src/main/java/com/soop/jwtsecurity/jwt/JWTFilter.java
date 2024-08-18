package com.soop.jwtsecurity.jwt;

import com.soop.jwtsecurity.dto.CustomOAuth2User;
import com.soop.jwtsecurity.entityDTO.UserEntity;
import com.soop.jwtsecurity.mapper.UserMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Tag(name = "JWT 필터" , description = "OncePerRequestFilter 커스텀")
public class JWTFilter extends OncePerRequestFilter {

    // JWT 유틸리티 클래스
    private final JWTUtil jwtUtil;
    // 사용자 정보를 조회하는 Mapper 인터페이스
    private final UserMapper userMapper;

    // 생성자를 통해 JWTUtil 및 UserMapper 객체를 주입
    public JWTFilter(JWTUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil; // JWTUtil 주입
        this.userMapper = userMapper; // UserMapper 주입
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 요청의 쿠키에서 액세스 토큰을 추출
        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        // 액세스 토큰이 없는 경우 필터 체인 진행
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 액세스 토큰이 만료된 경우 예외 처리
            if (jwtUtil.isExpired(accessToken)) {
                throw new ExpiredJwtException(null, null, "Access token is expired");
            }
        } catch (ExpiredJwtException e) {
            // 리프레시 토큰을 조회하여 검증
            String refreshToken = userMapper.searchRefreshEntity(jwtUtil.getSignupPlatformFromToken(accessToken));

            if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
                // 리프레시 토큰이 유효한 경우 새로운 액세스 토큰 생성
                String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
                String role = jwtUtil.getRoleFromRefreshToken(refreshToken);
                String newAccessToken = jwtUtil.generateAccessToken(username, role);

                // 새로운 액세스 토큰을 쿠키에 추가
                Cookie newAccessTokenCookie = new Cookie("access", newAccessToken);
                newAccessTokenCookie.setHttpOnly(false);
                newAccessTokenCookie.setPath("/");
                response.addCookie(newAccessTokenCookie);

                filterChain.doFilter(request, response);
                return;
            } else {
                // 리프레시 토큰이 유효하지 않은 경우 401 상태 반환
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.print("Access token is expired");
                return;
            }

        }

        // 토큰 카테고리가 'access'가 아닌 경우 예외 처리
        String category = jwtUtil.getCategory(accessToken);
        if (!"access".equals(category)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.print("Invalid access token");
            return;
        }

        // 토큰에서 사용자 정보 및 권한을 추출
        String signupPlatform = jwtUtil.getSignupPlatform(accessToken);
        String userRole = jwtUtil.getUserRole(accessToken);

        // 사용자 정보를 설정하고 인증 객체를 생성
        UserEntity userDTO = new UserEntity();
        userDTO.setSignupPlatform(signupPlatform);
        userDTO.setUserRole(userRole);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        // SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 필터 체인 진행
        filterChain.doFilter(request, response);
    }
}
