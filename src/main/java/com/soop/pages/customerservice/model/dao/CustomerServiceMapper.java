package com.soop.pages.customerservice.model.dao;

import com.soop.pages.customerservice.model.dto.FileDTO;
import com.soop.pages.customerservice.model.dto.InquiryDTO;
import com.soop.pages.customerservice.model.dto.NoticeMemberDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerServiceMapper {

    List<NoticeMemberDTO> csMainNoticeListAll();

    List<NoticeMemberDTO> noticeListAll();

    NoticeMemberDTO noticeDetail(String code);

    void inquiry(InquiryDTO inquiryDTO);

    FileDTO noticeFile(int id);
}
