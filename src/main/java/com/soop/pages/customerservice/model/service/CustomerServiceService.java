package com.soop.pages.customerservice.model.service;

import com.soop.pages.customerservice.model.dao.CustomerServiceMapper;
import com.soop.pages.customerservice.model.dto.FileDTO;
import com.soop.pages.customerservice.model.dto.InquiryDTO;
import com.soop.pages.customerservice.model.dto.NoticeMemberDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceService {

    @Autowired
    private CustomerServiceMapper customerServiceMapper;

    public List<NoticeMemberDTO> csMainNoticeList() {

        return customerServiceMapper.csMainNoticeListAll();
    }

    public List<NoticeMemberDTO> noticeList() {

        return customerServiceMapper.noticeListAll();
    }

    public NoticeMemberDTO noticeDetail(String code) {

        return customerServiceMapper.noticeDetail(code);
    }

    public void inquiry(InquiryDTO newInquiry) {
        customerServiceMapper.inquiry(newInquiry);
    }

    public FileDTO noticeFile(String code) {

        int id = Integer.parseInt(code);
        return customerServiceMapper.noticeFile(id);
    }
}
