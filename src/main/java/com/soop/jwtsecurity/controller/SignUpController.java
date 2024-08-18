package com.soop.jwtsecurity.controller;

import com.soop.jwtsecurity.dto.UserSignUpDTO;
import com.soop.jwtsecurity.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "회원 가입 컨트롤러")
@Controller
@ResponseBody
public class SignUpController {

    // UserMapper와 JWTUtil 객체를 주입받기 위한 필드 선언
    private final UserMapper userMapper;

    // 생성자를 통해 UserMapper와 JWTUtil 객체를 주입
    public SignUpController(UserMapper userMapper) {
        this.userMapper = userMapper; // UserMapper 주입
    }

    @Operation(summary = "추가 정보 입력 REST API", description = "자기소개가 null인 회원에게 추가 정보를 입력받아 저장하는 메서드")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpDTO userSignUpDTO) {
        // 클라이언트로부터 전달받은 추가 정보를 DB에 저장
        userMapper.saveAboutMe(userSignUpDTO.getAboutMe(), userSignUpDTO.getSignupPlatform(), userSignUpDTO.getNickName(), userSignUpDTO.getGender());

        // 회원가입 플랫폼을 기준으로 사용자 코드를 조회
        int userCode = userMapper.findBySignupPlatform(userSignUpDTO.getSignupPlatform()).getUserCode();

        // 사용자가 선택한 관심사를 가져와 DB에 저장
        List<Integer> interestCodes = userSignUpDTO.getSelectedInterests();
        for (Integer interestCode : interestCodes) {
            userMapper.saveUserInterest(userCode, interestCode); // 각 관심사 코드와 사용자 코드를 매핑하여 저장
        }

        // 회원 가입이 성공적으로 완료되었음을 클라이언트에 응답
        return ResponseEntity.ok("Sign up successful");
    }
}
