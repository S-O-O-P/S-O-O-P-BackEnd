package com.soop.jwtsecurity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name="로그아웃 컨트롤러")
@Controller
@ResponseBody
public class LogoutController {

    @Operation(summary = "로그아웃 REST API", description = "토큰 정보를 제거 하여 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, HttpSession session) {
        Cookie access = new Cookie("access", null); // access 쿠키 객체 생성
        access.setMaxAge(0); // 쿠키를 즉시 만료
        access.setHttpOnly(false); // httpOnly 설정을 끔 (httpOnly 를 true 로 했을 경우 javaScript 접근 불가.)
        access.setPath("/"); // 허용 경로 설정 (현재 도메인이 정해지지 않았고 로컬 환경이므로 "/" 전체 허용)
        response.addCookie(access); // cookie 를 담아서 응답
        session.invalidate(); // 세션 만료
        return new ResponseEntity<>(HttpStatus.OK); // 200코드 상태 응답
    }
}
