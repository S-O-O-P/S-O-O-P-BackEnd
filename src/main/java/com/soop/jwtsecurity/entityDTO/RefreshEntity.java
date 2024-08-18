package com.soop.jwtsecurity.entityDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;


@Tag(name = "리프레시 토큰 DTO")
@Getter
@Setter
public class RefreshEntity {

    private Long userCode;
    private int refreshCode;
    private String profilePic;
    private String signupPlatform;
    private String refresh;
    private String expiration;

}