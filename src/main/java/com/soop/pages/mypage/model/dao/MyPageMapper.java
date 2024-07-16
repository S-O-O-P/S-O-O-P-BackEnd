package com.soop.pages.mypage.model.dao;

import com.soop.pages.mypage.model.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyPageMapper {

    // 유저 평가 (평점 포함) 데이터 조회
    List<UserRatingDTO> getLoggedInUserRating(int userCode);

    // 유저 평가하기
    void evaluateUserRating(EvaluateUserRatingDTO newEvaluate);

    // 유저평가 항목 조회
    List<RatingDTO> getRatingList();

    // 내가 만든 허니팟 조회
    List<MyHoneypotDTO> getMyHoneypotList();

    List<ParticipatingHoneypotDTO> getParticipatingHoneypotList();

    List<MyCommentDTO> getMyComments();

    List<MyInquiryDTO> getMyInquiry(int userCode);

    List<FinishedHoneypotDTO> getFinishedHoneypot();

    List<InterestDTO> getInterest();

    UserProfileDTO getUserProfile(Integer userCode);

    void updateUserProfile(Integer userCode, String nickname, String aboutme, String profilePic);

    void deleteUserInterests(Integer userCode);



    List<RefreshDTO> getUserRef();

    void updateProfilePic(ProfilePicUpdateDTO profilePicUpdateDTO);

    void updateProfile(UserProfileDTO updateProfile);

    void insertUserInterests(@Param("userCode") Integer userCode, @Param("interestCodes") List<Integer> interestCodes);

    String getProfilePicFileName(Integer userCode);

    void updateProfilePicture(Integer userCode, String profilePicUrl);

    List<UserRatingDTO> getUserRatings(@Param("honeypotCode") int honeypotCode, @Param("raterCode") int raterCode);
}
