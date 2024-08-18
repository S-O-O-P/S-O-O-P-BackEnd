package com.soop.jwtsecurity.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "NAVER Oauth2 응답")
public class NaverResponse implements OAuth2Response {

    // 네이버 OAuth2 응답에서 받은 사용자 정보를 저장하는 맵
    private final Map<String, Object> attribute;

    // 생성자를 통해 네이버 OAuth2 응답 데이터를 초기화
    public NaverResponse(Map<String, Object> attribute) {
        // "response" 키로부터 사용자 정보를 초기화
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    // OAuth2 제공자 이름 반환
    @Override
    public String getProvider() {
        return "naver";
    }

    // 네이버에서 제공하는 고유 사용자 ID 반환
    @Override
    public String getProviderId() {
        return attribute != null && attribute.containsKey("id") ? attribute.get("id").toString() : null;
    }

    // 사용자의 이메일 주소 반환
    @Override
    public String getEmail() {
        return attribute != null && attribute.containsKey("email") ? attribute.get("email").toString() : null;
    }

    // 사용자의 닉네임 반환
    @Override
    public String getNickName() {
        return attribute != null && attribute.containsKey("nickname") ? attribute.get("nickname").toString() : null;
    }

    // 사용자의 프로필 이미지 URL 반환
    @Override
    public String getProfileImage() {
        return attribute != null && attribute.containsKey("profile_image") ? attribute.get("profile_image").toString() : null;
    }

    // 사용자의 성별 반환
    @Override
    public String getGender() {
        return attribute != null && attribute.containsKey("gender") ? attribute.get("gender").toString() : null;
    }
}
