package com.soop.pages.main.model.service;

import com.soop.pages.main.model.dao.MainMapper;
import com.soop.pages.main.model.dto.UserInterestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainService {
  private final MainMapper mainMapper;

  public MainService(MainMapper mainMapper) {this.mainMapper = mainMapper;}

  public List<UserInterestDTO> selectUserInterestByCode(int userCode) {
    return mainMapper.selectUserInterestByCode(userCode);
  }
}
