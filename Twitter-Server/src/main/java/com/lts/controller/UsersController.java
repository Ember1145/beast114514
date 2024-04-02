package com.lts.controller;


import cn.hutool.core.bean.BeanUtil;
import com.lts.domain.dto.userDTO;
import com.lts.domain.po.Users;
import com.lts.service.IUsersService;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.PostMapping;
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
@MapperScan("com.lts.mapper")
public class UsersController {
    private final IUsersService usersService;
@PostMapping("/register")
    public void saveOrUpdate(@RequestBody userDTO userDTO) {
        usersService.userRegister(userDTO);
    }

}
