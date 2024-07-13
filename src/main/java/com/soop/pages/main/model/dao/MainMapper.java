package com.soop.pages.main.model.dao;

import com.soop.pages.main.model.dto.UserInterestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MainMapper {

  List<UserInterestDTO> selectUserInterestByCode(int userCode);
}
