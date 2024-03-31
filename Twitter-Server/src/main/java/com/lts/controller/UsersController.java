package com.lts.controller;


import cn.hutool.core.bean.BeanUtil;
import com.lts.domain.dto.userDTO;
import com.lts.domain.po.Users;
import com.lts.service.IUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author author
 * @since 2024-03-29
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final IUsersService usersService;


    public void saveOrUpdate(@RequestBody userDTO userDTO) {

        Users users = BeanUtil.copyProperties(userDTO, Users.class);
        usersService.save(users);
    }

}
