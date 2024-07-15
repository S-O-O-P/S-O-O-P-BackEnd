package com.soop.jwtsecurity.mapper;

import com.soop.jwtsecurity.entityDTO.RefreshEntity;
import com.soop.jwtsecurity.entityDTO.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Tag(name = "XML 맵퍼")
@Mapper
public interface UserMapper {

    @Operation(summary = "플랫폼 정보 찾기", description = "")
    UserEntity findBySignupPlatform(String signupPlatform);
    @Operation(summary = "자기소개 정보 찾기", description = "")
    UserEntity findAboutMe(String signupPlatform);
    @Operation(summary = "리프레쉬 토큰 삭제", description = "")
    void deleteByRefresh(@Param("refresh") String refresh);
    @Operation(summary = "유저 정보 입력", description = "")
    void saveUserEntity(UserEntity userEntity);
    @Operation(summary = "리프레쉬 토큰 저장", description = "")
    void saveRefreshEntity(RefreshEntity refreshEntity);
    @Operation(summary = "리프레쉬 토큰 찾기", description = "")
    String searchRefreshEntity(String signupPlatform);
    @Operation(summary = "자기소개 저장", description = "")
    void saveAboutMe(@Param("aboutme") String aboutme, @Param("signupPlatform") String signupPlatform,@Param("nickname") String nickname);
    @Operation(summary = "관심사 정보 저장", description = "")
    void saveUserInterest(@Param("userCode") int userCode, @Param("interestCode") int interestCode);
    @Operation(summary = "프로파일 사진 찾기", description = "")
    String getProfilePic(String signupPlatform);
}
