package com.soop.jwtsecurity.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "NAVER Oauth2 응답")
public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute != null && attribute.containsKey("id") ? attribute.get("id").toString() : null;
    }

    @Override
    public String getEmail() {
        return attribute != null && attribute.containsKey("email") ? attribute.get("email").toString() : null;
    }

    @Override
    public String getNickName() {
        return attribute != null && attribute.containsKey("nickname") ? attribute.get("nickname").toString() : null;
    }

    @Override
    public String getProfileImage() {
        return attribute != null && attribute.containsKey("profile_image") ? attribute.get("profile_image").toString() : null;
    }

    @Override
    public String getGender() {
        return attribute != null && attribute.containsKey("gender") ? attribute.get("gender").toString() : null;
    }
}
