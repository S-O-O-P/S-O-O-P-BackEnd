package com.soop.pages.honeypot.model.dao;

import com.soop.pages.honeypot.model.dto.CommentAndLinkBeeUserDTO;
import com.soop.pages.honeypot.model.dto.CommentDTO;
import com.soop.pages.honeypot.model.dto.HoneypotAndInterestAndLinkBeeUserDTO;
import com.soop.pages.honeypot.model.dto.HoneypotDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HoneyPotMapper {

    // 허니팟 등록하기
    void insertHoneypot(HoneypotDTO honeypotDTO);

    // 허니팟 전체 조회
    List<HoneypotDTO> findAllHoneypots();

    // 허니팟 상세 조회
    HoneypotAndInterestAndLinkBeeUserDTO findByHoneypotCode(int honeypotCode);

    // 상세페이지 수정용 임시 상세조회
    HoneypotDTO temporaryFindByHoneypotCode(int honeypotCode);

    // 상세페이지 수정
    void modifyHoneypot(HoneypotDTO modifyHoneypot);

    // 댓글 전체 조회
    List<CommentAndLinkBeeUserDTO> findAllComments();

    // 댓글 코드로 조회
    CommentAndLinkBeeUserDTO findCommentByCommentCode(int commentCode);

    // 댓글 코드로 조회(임시)
    CommentDTO temporaryFindCommentByCommentCode(int commentCode);

    // 댓글 수정
    void modifyComment(CommentDTO modifyComment);

    // 댓글 삭제
    void deleteCommentByCommentCode(int commentCode);

    // 댓글 등록
    void insertComment(CommentDTO comment);

    void registComment(CommentAndLinkBeeUserDTO newComment);
}


