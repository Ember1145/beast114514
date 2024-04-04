package com.lts.controller.user;


import cn.hutool.core.bean.BeanUtil;
import com.lts.domain.dto.userDTO;
import com.lts.domain.vo.userVO;
import com.lts.result.Result;
import com.lts.service.IUsersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "登录接口")
public class UsersController {
    private final IUsersService usersService;
@PostMapping("/register")
@ApiOperation("登录功能")
    public Result<userVO> saveOrUpdate(@RequestBody userDTO userDTO) {
    userVO userVO = usersService.userRegister(userDTO);
    return Result.success(userVO);
    }


    public Result
}
