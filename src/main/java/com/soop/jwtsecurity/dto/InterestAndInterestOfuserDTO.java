package com.soop.jwtsecurity.dto;

import com.soop.pages.honeypot.model.dto.InterestDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;

@Tag(name = "관심사 정보 DTO")
@Getter
@Setter
public class InterestAndInterestOfuserDTO {

    private InterestDTO interestDTO;
    private int interestCode;
    private int userCode;
}
