package com.soop.pages.cultureinfo.model.service;

import com.soop.pages.cultureinfo.model.dao.CultureInfoMapper;
import com.soop.pages.cultureinfo.model.dto.CultureInfoDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CultureInfoService {
  private final CultureInfoMapper cultureInfoMapper;

  public CultureInfoService(CultureInfoMapper cultureInfoMapper) {this.cultureInfoMapper = cultureInfoMapper;}

  // 얼리버드 공연/전시 정보 전체 조회
  public List<CultureInfoDTO> selectAllCultureInfo() {return cultureInfoMapper.selectAllCultureInfo();}

  public CultureInfoDTO selectEarlyInfoByEarlyBirdCode(int earlyCode) {return cultureInfoMapper.selectEarlyInfoByEarlyBirdCode(earlyCode);
  }
}
