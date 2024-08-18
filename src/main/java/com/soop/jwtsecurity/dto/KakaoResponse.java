package com.soop.jwtsecurity.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "KAKAO Oauth2 응답")
public class KakaoResponse implements OAuth2Response {

    // 사용자의 카카오 계정 정보를 저장하는 맵
    private final Map<String, Object> account;

    // 카카오에서 제공하는 고유 사용자 ID
    private final String providerId;

    // 사용자의 프로필 정보를 저장하는 맵
    private final Map<String, Object> properties;

    // 생성자를 통해 카카오 OAuth2 응답 데이터를 초기화
    public KakaoResponse(Map<String, Object> attributes) {
        // "kakao_account" 키로부터 사용자 계정 정보를 초기화
        this.account = (Map<String, Object>) attributes.get("kakao_account");

        // "id" 키로부터 고유 사용자 ID를 문자열로 변환하여 초기화
        this.providerId = attributes.get("id").toString();

        // "properties" 키로부터 사용자 프로필 정보를 초기화
        this.properties = (Map<String, Object>) attributes.get("properties");
    }

    // OAuth2 제공자 이름 반환
    @Override
    public String getProvider() {
        return "kakao";
    }

    // 카카오에서 제공하는 고유 사용자 ID 반환
    @Override
    public String getProviderId() {
        return this.providerId;
    }

    // 사용자의 이메일 주소 반환
    @Override
    public String getEmail() {
        return account.containsKey("email") ? account.get("email").toString() : null;
    }

    // 사용자의 닉네임 반환
    @Override
    public String getNickName() {
        return properties != null && properties.containsKey("nickname") ? properties.get("nickname").toString() : null;
    }

    // 사용자의 프로필 이미지 URL 반환
    @Override
    public String getProfileImage() {
        return properties != null && properties.containsKey("profile_image") ? properties.get("profile_image").toString() : null;
    }

    // 사용자의 성별 반환
    @Override
    public String getGender() {
        return account.containsKey("gender") ? account.get("gender").toString() : null;
    }
}
