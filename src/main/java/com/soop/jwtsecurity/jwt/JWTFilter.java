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

    private final JWTUtil jwtUtil;
    private final UserMapper userMapper;

    public JWTFilter(JWTUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(accessToken)) {
                throw new ExpiredJwtException(null, null, "Access token is expired");
            }
        } catch (ExpiredJwtException e) {
            String refreshToken = userMapper.searchRefreshEntity(jwtUtil.getSignupPlatformFromToken(accessToken));
            if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
                String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
                String role = jwtUtil.getRoleFromRefreshToken(refreshToken);
                String newAccessToken = jwtUtil.generateAccessToken(username, role);

                Cookie newAccessTokenCookie = new Cookie("access", newAccessToken);
                newAccessTokenCookie.setHttpOnly(false);
                newAccessTokenCookie.setPath("/");
                response.addCookie(newAccessTokenCookie);

                filterChain.doFilter(request, response);
                return;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.print("Access token is expired");
                return;
            }

        }

        String category = jwtUtil.getCategory(accessToken);
        if (!"access".equals(category)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.print("Invalid access token");
            return;
        }

        String signupPlatform = jwtUtil.getSignupPlatform(accessToken);
        String userRole = jwtUtil.getUserRole(accessToken);

        UserEntity userDTO = new UserEntity();
        userDTO.setSignupPlatform(signupPlatform);
        userDTO.setUserRole(userRole);

        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
