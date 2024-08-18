package com.soop.jwtsecurity.dto;

import com.soop.jwtsecurity.entityDTO.UserEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Tag(name = "OAuth2User 를 커스텀 하기위한 class", description = "OAuth2User implements 로 받아서 커스텀")
public class CustomOAuth2User implements OAuth2User {

    // UserEntity 객체를 통해 사용자 정보를 관리
    private final UserEntity userDTO;

    // 생성자에서 UserEntity 객체를 초기화
    public CustomOAuth2User(UserEntity userDTO) {
        this.userDTO = userDTO;
    }

    // OAuth2 사용자 정보의 속성을 반환하는 메서드
    // 현재는 null을 반환
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // 사용자의 권한 정보를 반환하는 메서드
    // UserEntity 객체의 역할 정보를 GrantedAuthority 컬렉션으로 변환하여 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>(); // 권한 정보를 저장할 컬렉션 생성

        // UserEntity 객체의 역할 정보를 GrantedAuthority로 변환하여 컬렉션에 추가
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userDTO.getUserRole();
            }
        });

        return collection; // 권한 정보 컬렉션 반환
    }

    // 사용자의 이름을 반환하는 메서드
    @Override
    public String getName() {
        return userDTO.getNickName();
    }

    // 사용자의 회원가입 플랫폼 정보를 반환하는 메서드
    public String getUsername() {
        return userDTO.getSignupPlatform();
    }

    // 사용자의 코드 정보를 반환하는 메서드
    public int getUserCode() {
        return userDTO.getUserCode();
    }
}
