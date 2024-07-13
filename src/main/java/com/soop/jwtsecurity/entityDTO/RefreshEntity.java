package com.soop.jwtsecurity.entityDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


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