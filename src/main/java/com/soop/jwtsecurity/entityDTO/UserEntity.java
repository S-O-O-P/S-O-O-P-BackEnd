package com.soop.jwtsecurity.entityDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Tag(name = "유저 정보 DTO")
@Getter
@Setter
public class UserEntity {

    private int userCode;
    private String nickName;
    private String email;
    private String gender;
    private String profilePic;
    private String aboutMe;
    private String userRole;
    private Date signupDate;
    private String signupPlatform;

}