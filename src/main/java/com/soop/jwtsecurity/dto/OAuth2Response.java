package com.soop.jwtsecurity.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Oauth2 응답 인터페이스")
public interface OAuth2Response {

    //제공자 (Ex. naver, google, ...)
    String getProvider();
    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();
    //이메일
    String getEmail();
    //사용자 실명 (설정한 이름)
    String getNickName();
    //성별
    String getGender();
    //프로필 이미지
    String getProfileImage();


}