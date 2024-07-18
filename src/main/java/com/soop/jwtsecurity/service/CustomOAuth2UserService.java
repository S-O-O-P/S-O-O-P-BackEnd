
package com.soop.jwtsecurity.service;

import com.soop.jwtsecurity.dto.*;
import com.soop.jwtsecurity.entityDTO.UserEntity;
import com.soop.jwtsecurity.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Tag(name = "커스텀 OAuth2 서비스", description = "DefaultOAuth2UserService 활용")
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;

    public CustomOAuth2UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Operation(summary = "OAuth2 유저 확인", description = "naver,google,kakao 등 플랫폼 비교")
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String signupPlatform = oAuth2Response.getProvider() +":"+ oAuth2Response.getProviderId();
//        System.out.println("signupPlatform = " + signupPlatform);
        UserEntity existData = userMapper.findBySignupPlatform(signupPlatform);

        if (existData == null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setSignupPlatform(signupPlatform);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setNickName(oAuth2Response.getNickName());
            userEntity.setUserRole("ROLE_USER");
            userEntity.setProfilePic(oAuth2Response.getProfileImage() != null ? oAuth2Response.getProfileImage() : "default_profile_image_url"); // 기본 프로필 이미지 URL 설정
            userEntity.setAboutMe(null);
            userEntity.setGender(convertGender(oAuth2Response.getGender() != null ? oAuth2Response.getGender() : "구글일 경우 성별 입력받기 메소드 추가가 될 자리"));
            userEntity.setSignupDate(new Date());

            userMapper.saveUserEntity(userEntity);

            UserEntity userDTO = new UserEntity();
            userDTO.setSignupPlatform(signupPlatform);
            userDTO.setNickName(oAuth2Response.getNickName());
            userDTO.setUserRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        } else {
            existData.setEmail(oAuth2Response.getEmail());
            existData.setNickName(oAuth2Response.getNickName());
            UserEntity userDTO = new UserEntity();

            userDTO.setEmail(existData.getEmail());
            userDTO.setNickName(existData.getNickName());
            userDTO.setSignupPlatform(existData.getSignupPlatform());
            userDTO.setUserRole(existData.getUserRole());

            return new CustomOAuth2User(userDTO);
        }
    }
    @Operation(summary = "성별 번역", description = "성별 ex)m,male = 남자")
    private String convertGender(String gender) {
        if (gender == null) {
            return "";
        }
        switch (gender.toLowerCase()) {
            case "m":
            case "male":
                return "남자";
            case "f":
            case "female":
                return "여자";
            default:
                return "";
        }
    }
}
