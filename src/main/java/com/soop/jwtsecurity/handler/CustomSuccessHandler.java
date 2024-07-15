
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

@Tag(name = "커스텀 로그인 성공 핸들러" , description = "SimpleUrlAuthenticationSuccessHandler 커스텀")
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserMapper userMapper;

    public CustomSuccessHandler(JWTUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        int userCode = userMapper.findBySignupPlatform(username).getUserCode();
        String profilePic = userMapper.findBySignupPlatform(username).getProfilePic();


        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();


        String access = jwtUtil.createJwt("access", username, role, userCode, profilePic, 300L * 1000); // 10분 (600초)

        String existingRefreshToken = userMapper.searchRefreshEntity(username);

        if (existingRefreshToken != null) {
            userMapper.deleteByRefresh(existingRefreshToken);
        }

        String refresh = jwtUtil.createJwt("refresh", username, role, userCode, profilePic, 86400L *1000); // 24시간 (86400000밀리초)
        addRefreshEntity(username, refresh, 86400L*1000);

        System.out.println("access = " + access);
        System.out.println("refresh = " + refresh);
        System.out.println("userCode = " + userCode);

        // 리프레시 토큰을 HTTP-Only 쿠키로 저장
//        createAndAddCookie(response, "refresh", refresh);
        // 엑세스 토큰을 쿠키로 저장
        createAndAddCookie(response, "access", access);
//        createAndAddCookie(response, "username", username);

        UserEntity userEntity = new UserEntity();

        //최초 가입 확인(aboutMe 유무에 따라 나누기)
        if(userMapper.findAboutMe(username) == null){
            response.sendRedirect("http://localhost:3001/signup");
        }else {
            response.sendRedirect("http://localhost:3001/main");
        }


    }

    private void addRefreshEntity(String signupPlatform, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setSignupPlatform(signupPlatform);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());
        userMapper.saveRefreshEntity(refreshEntity);
    }

    private void createAndAddCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(5 * 60); // 10분
        cookie.setDomain("localhost");
        cookie.setHttpOnly(false); // JavaScript에서 접근 가능하도록 설정
        cookie.setPath("/");
        cookie.setSecure(false); // localhost 환경에서는 false, 실제 배포 시 true로 설정

        response.addCookie(cookie);

        // SameSite 설정 추가
//        response.setHeader("Set-Cookie",
//                String.format("%s=%s; Max-Age=%d; Domain=%s; Path=%s; HttpOnly; SameSite=None; Secure",
//                        key, value, 10 * 60, "localhost", "/"));
    }

}

