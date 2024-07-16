package com.soop.jwtsecurity.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Tag(name = "유저 추가정보 DTO")
@Getter
@Setter
public class UserSignUpDTO {
    private String aboutMe;
    private String nickName;
    private String gender;
    private String signupPlatform;
    private List<Integer> selectedInterests;
}
