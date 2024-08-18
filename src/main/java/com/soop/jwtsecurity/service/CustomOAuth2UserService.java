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

    // UserMapper를 사용하여 데이터베이스와의 상호작용을 처리
    private final UserMapper userMapper;

    // UserMapper를 주입하는 생성자
    public CustomOAuth2UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Operation(summary = "OAuth2 유저 확인", description = "naver,google,kakao 등 플랫폼 비교")
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 부모 클래스의 loadUser 메서드를 사용하여 OAuth2User 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 클라이언트 등록 ID를 가져옴 (예: naver, google, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        // 플랫폼에 따라 적절한 OAuth2Response 구현체를 선택
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null; // 지원하지 않는 플랫폼일 경우 null 반환
        }

        // 가입 플랫폼을 생성 (예: google:123456789)
        String signupPlatform = oAuth2Response.getProvider() + ":" + oAuth2Response.getProviderId();
//        System.out.println("signupPlatform = " + signupPlatform);

        // 데이터베이스에서 해당 사용자가 존재하는지 확인
        UserEntity existData = userMapper.findBySignupPlatform(signupPlatform);

        // 사용자가 존재하지 않을 경우 새 사용자 등록
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

            // 사용자 정보를 데이터베이스에 저장
            userMapper.saveUserEntity(userEntity);

            UserEntity userDTO = new UserEntity();
            userDTO.setSignupPlatform(signupPlatform);
            userDTO.setNickName(oAuth2Response.getNickName());
            userDTO.setUserRole("ROLE_USER");

            // 새 사용자 정보를 반환
            return new CustomOAuth2User(userDTO);
        } else {
            // 기존 사용자가 있을 경우 정보를 업데이트
            existData.setEmail(oAuth2Response.getEmail());
            existData.setNickName(oAuth2Response.getNickName());
            UserEntity userDTO = new UserEntity();

            userDTO.setEmail(existData.getEmail());
            userDTO.setNickName(existData.getNickName());
            userDTO.setSignupPlatform(existData.getSignupPlatform());
            userDTO.setUserRole(existData.getUserRole());

            // 기존 사용자 정보를 반환
            return new CustomOAuth2User(userDTO);
        }
    }

    @Operation(summary = "성별 번역", description = "성별 ex)m,male = 남자")
    // 성별 정보를 번역하는 메서드
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
