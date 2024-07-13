package com.soop.jwtsecurity.mapper;

import com.soop.jwtsecurity.entityDTO.RefreshEntity;
import com.soop.jwtsecurity.entityDTO.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    UserEntity findBySignupPlatform(String signupPlatform);
    UserEntity findAboutMe(String signupPlatform);

//    UserEntity findUserCode(int usercode);

    Boolean existsByRefresh(@Param("refresh") String refresh);

    void deleteByRefresh(@Param("refresh") String refresh);

    void saveUserEntity(UserEntity userEntity);

    void saveRefreshEntity(RefreshEntity refreshEntity);

    String searchRefreshEntity(String signupPlatform);

    String googleGender(String gender);
    void saveAboutMe(@Param("aboutme") String aboutme, @Param("signupPlatform") String signupPlatform,@Param("nickname") String nickname);

    void saveUserInterest(@Param("userCode") int userCode, @Param("interestCode") int interestCode);

    String getProfilePic(String signupPlatform);
}
