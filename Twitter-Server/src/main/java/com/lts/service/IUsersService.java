package com.lts.service;

import com.lts.domain.dto.userDTO;
import com.lts.domain.po.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lts.domain.vo.tokenVO;
import com.lts.domain.vo.userPageVO;

import java.util.List;


public interface IUsersService extends IService<Users> {

void userRegister(userDTO userDTO);

    tokenVO login(userDTO userDTO);

    List<Users> findUserByEmailOrUsername(String qu);

    userPageVO queryUser(String emailCut, Long userId);
}
