package com.soop.jwtsecurity.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "KAKAO Oauth2 응답")
public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> account;
    private final String providerId;
    private final Map<String, Object> properties;

    public KakaoResponse(Map<String, Object> attributes) {
        this.account = (Map<String, Object>) attributes.get("kakao_account");
        this.providerId = attributes.get("id").toString();
        this.properties = (Map<String, Object>) attributes.get("properties");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return this.providerId;
    }

    @Override
    public String getEmail() {
        return account.containsKey("email") ? account.get("email").toString() : null;
    }

    @Override
    public String getNickName() {
        return properties != null && properties.containsKey("nickname") ? properties.get("nickname").toString() : null;
    }

    @Override
    public String getProfileImage() {
        return properties != null && properties.containsKey("profile_image") ? properties.get("profile_image").toString() : null;
    }

    @Override
    public String getGender() {
        return account.containsKey("gender") ? account.get("gender").toString() : null;
    }
}
