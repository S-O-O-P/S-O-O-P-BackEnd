package com.soop.pages.cultureinfo.model.dao;

import com.soop.pages.cultureinfo.model.dto.CultureInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CultureInfoMapper {

  List<CultureInfoDTO> selectAllCultureInfo();

  CultureInfoDTO selectEarlyInfoByEarlyBirdCode(int earlyCode);
}
