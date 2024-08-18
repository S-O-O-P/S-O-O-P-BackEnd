package com.soop.jwtsecurity.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Google Oauth2 응답")
public class GoogleResponse implements OAuth2Response {

    // Google OAuth2 응답에서 받은 사용자 정보를 저장하는 맵
    private final Map<String, Object> attribute;

    // 생성자를 통해 Google OAuth2 응답 데이터를 초기화
    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    // OAuth2 제공자 이름 반환
    @Override
    public String getProvider() {
        return "google";
    }

    // Google에서 제공하는 고유 사용자 ID 반환
    @Override
    public String getProviderId() {
        return attribute.containsKey("sub") ? attribute.get("sub").toString() : null;
    }

    // 사용자의 이메일 주소 반환
    @Override
    public String getEmail() {
        return attribute.containsKey("email") ? attribute.get("email").toString() : null;
    }

    // 사용자의 닉네임 반환
    @Override
    public String getNickName() {
        return attribute.containsKey("name") ? attribute.get("name").toString() : null;
    }

    //구글은 따로 성별 받는 부분 사용해서 입력 받기로 수정
    @Override
    public String getGender() {
        return attribute.containsKey("gender") ? attribute.get("gender").toString() : null;
    }

    // 사용자의 프로필 이미지 URL 반환
    @Override
    public String getProfileImage() {
        return attribute.containsKey("picture") ? attribute.get("picture").toString() : null;
    }
}
