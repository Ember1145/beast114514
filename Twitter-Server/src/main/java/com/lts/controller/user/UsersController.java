package com.lts.controller.user;


import cn.hutool.core.bean.BeanUtil;
import com.lts.Properties.JwtProperties;
import com.lts.constant.JwtClaimsConstant;
import com.lts.domain.dto.userDTO;
import com.lts.domain.po.Users;
import com.lts.domain.vo.tokenVO;
import com.lts.domain.vo.userVO;
import com.lts.exception.UserLostException;
import com.lts.result.Result;
import com.lts.service.IUsersService;
import com.lts.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
@Api(tags = "登录接口")
public class UsersController {
    private final IUsersService usersService;


    @PostMapping("/register")
    @ApiOperation("注册功能")
    public Result<userVO> saveOrUpdate(@RequestBody userDTO userDTO) {
        userVO userVO = usersService.userRegister(userDTO);
        return Result.success(userVO);
    }

    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<tokenVO> login(@RequestBody userDTO userDTO) {
        tokenVO tokenVo= usersService.login(userDTO);
        return Result.success(tokenVo);

    }
}