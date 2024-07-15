package com.soop.jwtsecurity.controller;

import com.soop.jwtsecurity.dto.UserSignUpDTO;
import com.soop.jwtsecurity.jwt.JWTUtil;
import com.soop.jwtsecurity.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="회원 가입 컨트롤러")
@Controller
@ResponseBody
public class SignUpController {
    private final UserMapper userMapper;
    private final JWTUtil jwtUtil;

    public SignUpController(UserMapper userMapper,JWTUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "추가 정보 입력 REST API", description = "자기소개가 null 값이 회원에게 추가 정보 입력 받는 메소드")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpDTO userSignUpDTO) {
        userMapper.saveAboutMe(userSignUpDTO.getAboutMe(), userSignUpDTO.getSignupPlatform(), userSignUpDTO.getNickName());
        int userCode = userMapper.findBySignupPlatform(userSignUpDTO.getSignupPlatform()).getUserCode();

        // Save interests
        List<Integer> interestCodes = userSignUpDTO.getSelectedInterests();
        for (Integer interestCode : interestCodes) {
            userMapper.saveUserInterest(userCode, interestCode);
        }

//        System.out.println("userCode: "+ userCode);
//        System.out.println("Received aboutMe: " + userSignUpDTO.getAboutMe());
//        System.out.println("Received signupPlatform: " + userSignUpDTO.getSignupPlatform());
//        System.out.println("Received nickName: " + userSignUpDTO.getNickName());
//        System.out.println("Received selectedInterests: " + userSignUpDTO.getSelectedInterests());

        return ResponseEntity.ok("Sign up successful");
    }
}
