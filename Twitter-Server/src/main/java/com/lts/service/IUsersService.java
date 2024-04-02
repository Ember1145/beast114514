package com.lts.service;

import com.lts.domain.dto.userDTO;
import com.lts.domain.po.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;


public interface IUsersService extends IService<Users> {

    void userRegister(userDTO userDTO);
}
