package com.soop.jwtsecurity.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Google Oauth2 응답")
public class GoogleResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.containsKey("sub") ? attribute.get("sub").toString() : null;
    }

    @Override
    public String getEmail() {
        return attribute.containsKey("email") ? attribute.get("email").toString() : null;
    }

    @Override
    public String getNickName() {
        return attribute.containsKey("name") ? attribute.get("name").toString() : null;
    }

    //구글은 따로 성별 받는 부분 사용 해야 해서 입력 받기로 수정
    @Override
    public String getGender() {
        return attribute.containsKey("gender") ? attribute.get("gender").toString() : null;
    }

    @Override
    public String getProfileImage() {
        return attribute.containsKey("picture") ? attribute.get("picture").toString() : null;
    }
}
