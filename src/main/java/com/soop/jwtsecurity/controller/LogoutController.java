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
        Cookie access = new Cookie("access", null);
        access.setMaxAge(0); // 쿠키를 즉시 만료
        access.setHttpOnly(false);
        access.setPath("/");
        response.addCookie(access);
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
