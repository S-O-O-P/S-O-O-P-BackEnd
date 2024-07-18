package com.soop.pages.mypage.model.service;

import com.soop.pages.mypage.model.dao.MyPageMapper;
import com.soop.pages.mypage.model.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MyPageService {

    private MyPageMapper myPageMapper;
    private static final Logger logger = LoggerFactory.getLogger(MyPageService.class);

    public MyPageService(MyPageMapper myPageMapper) {
        this.myPageMapper = myPageMapper;
    }

    public List<UserRatingDTO> getLoggedInUserRating(int userCode) {
        return myPageMapper.getLoggedInUserRating(userCode);
    }

    public EvaluateUserRatingDTO evaluateUserRating(EvaluateUserRatingDTO newEvaluate) {

        myPageMapper.evaluateUserRating(newEvaluate);
        return newEvaluate;
    }

    public List<RatingDTO> getRatingList() {
        return myPageMapper.getRatingList();
    }

    public List<MyHoneypotDTO> getMyHoneypotList() {
        return myPageMapper.getMyHoneypotList();
    }

    public List<ParticipatingHoneypotDTO> getParticipatingHoneypotList() {
        return myPageMapper.getParticipatingHoneypotList();
    }

    public List<MyCommentDTO> getMyComments() {
        return myPageMapper.getMyComments();
    }

    public List<MyInquiryDTO> getMyInquiry(int userCode) {
        return myPageMapper.getMyInquiry(userCode);
    }

    public List<FinishedHoneypotDTO> getFinishedHoneypot() {
        return myPageMapper.getFinishedHoneypot();
    }

    public List<InterestDTO> getInterest() {
        return myPageMapper.getInterest();
    }

    public UserProfileDTO getUserProfile(Integer userCode) {
        return myPageMapper.getUserProfile(userCode);
    }

//    @Transactional
//    public void updateUserProfile(Integer userCode, UserProfileUpdateDTO dto) {
//        // 사용자 정보 업데이트
//        myPageMapper.updateUserProfile(userCode, dto.getNickname(), dto.getAboutme(), dto.getProfilePic());
//
//        // 기존 관심사 삭제
//        myPageMapper.deleteUserInterests(userCode);
//
//        // 새 관심사 추가
//        if (dto.getInterests() != null && !dto.getInterests().isEmpty()) {
//            myPageMapper.insertUserInterests(userCode, dto.getInterests());
//        }
//    }

    public List<RefreshDTO> getUserRef() {
        return myPageMapper.getUserRef();
    }

    @Value("${file.upload-dir}")
    private String uploadDir;
    public String updateProfilePic(Integer userCode, MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 새 파일 저장
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String profilePicUrl = "/images/mypage/saveProfilePic/" + fileName;
        ProfilePicUpdateDTO profileUpdateDTO = new ProfilePicUpdateDTO();
        profileUpdateDTO.setUserCode(userCode);
        profileUpdateDTO.setProfilePic(profilePicUrl);
        myPageMapper.updateProfilePic(profileUpdateDTO);

        return profilePicUrl;
    }

    @Transactional
//    public void updateUserProfile(Integer userCode, UserProfileUpdateDTO dto) {
//        // 사용자 정보 업데이트
//        myPageMapper.updateUserProfile(userCode, dto.getNickname(), dto.getAboutme(), dto.getProfilePic());
//
//        // 기존 관심사 삭제
//        myPageMapper.deleteUserInterests(userCode);
//
//        // 새 관심사 추가
//        if (dto.getInterests() != null && !dto.getInterests().isEmpty()) {
//            myPageMapper.insertUserInterests(userCode, dto.getInterests());
//        }
//    }
    public UserProfileDTO updateProfile(Integer userCode, UserProfileDTO dto) {

        // 기존 유저 프로필 정보 가져오기
        UserProfileDTO updateProfile = myPageMapper.getUserProfile(userCode);

        // 수정할 항목들
        updateProfile.setNickname(dto.getNickname());
        updateProfile.setAboutme(dto.getAboutme());
        updateProfile.setInterests(dto.getInterests());

        myPageMapper.updateProfile(updateProfile);

        // 관심사 업데이트
        myPageMapper.deleteUserInterests(userCode);
        if (dto.getInterests() != null && !dto.getInterests().isEmpty()) {
            List<Integer> interestCodes = dto.getInterests().stream()
                    .map(InterestDTO::getInterestCode)
                    .collect(Collectors.toList());
            myPageMapper.insertUserInterests(userCode, interestCodes);
        }

        return myPageMapper.getUserProfile(userCode);
    }

    public void updateProfilePicture(Integer userCode, String profilePicUrl) {
        ProfilePicUpdateDTO dto = new ProfilePicUpdateDTO();
        dto.setUserCode(userCode);
        dto.setProfilePic(profilePicUrl);
        myPageMapper.updateProfilePic(dto);
    }

    public List<UserRatingDTO> getUserRatings(int honeypotCode, int raterCode) {
        return myPageMapper.getUserRatings(honeypotCode, raterCode);
    }
}
