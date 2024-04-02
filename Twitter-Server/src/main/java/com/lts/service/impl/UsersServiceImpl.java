package com.lts.service.impl;


import com.lts.domain.dto.userDTO;
import com.lts.domain.po.Users;
import com.lts.mapper.UsersMapper;
import com.lts.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-03-29
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    @Override
    public void userRegister(userDTO userDTO) {
   Users users= lambdaQuery().eq(Users::getEmail, userDTO.getEmail())
                .getEntity();
   if(userDTO.getEmail().equals(users.getEmail()))
   {
       throw new User
}}
