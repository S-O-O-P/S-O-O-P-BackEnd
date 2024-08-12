
package com.soop.jwtsecurity.dto;

import com.soop.jwtsecurity.entityDTO.UserEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Tag(name = "OAuth2User 를 커스텀 하기위한 class" , description = "OAuth2User implements 로 받아서 커스텀")
public class CustomOAuth2User implements OAuth2User {

    private final UserEntity userDTO;

    public CustomOAuth2User(UserEntity userDTO) {

        this.userDTO = userDTO;
    }


    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userDTO.getUserRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return userDTO.getNickName();
    }

    public String getUsername() {

        return userDTO.getSignupPlatform();
    }

    public int getUserCode() {

        return userDTO.getUserCode();
    }
}
