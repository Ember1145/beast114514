package com.lts.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lts.Properties.JwtProperties;
import com.lts.constant.JwtClaimsConstant;
import com.lts.constant.MessageConstant;
import com.lts.domain.dto.userDTO;
import com.lts.domain.po.Users;
import com.lts.domain.vo.tokenVO;
import com.lts.domain.vo.userVO;
import com.lts.exception.UserExistsException;
import com.lts.exception.UserLostException;
import com.lts.mapper.UsersMapper;
import com.lts.result.Result;
import com.lts.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lts.utils.JwtUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-03-29
 */
@Service
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    @Autowired
    public UsersMapper usersMapper;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public userVO userRegister(userDTO userDTO) {

        // 首先检查用户是否已经存在
        Users existingUser = usersMapper.selectOne(new QueryWrapper<Users>().eq("email", userDTO.getEmail()));
        if (existingUser != null) {
            throw new UserExistsException(MessageConstant.ALREADY_EXISTS);
        } else {
            // 如果用户不存在，则将 DTO 转换为实体类并插入数据库
            Users newUser = BeanUtil.copyProperties(userDTO, Users.class);
            newUser.setCreatedAt(LocalDateTime.now());
            usersMapper.insert(newUser);
            userVO userVO = BeanUtil.copyProperties(newUser, com.lts.domain.vo.userVO.class);
//                log.info("DTO: {}", userDTO);
//                log.info("New User: {}", newUser);
            return userVO;
        }
    }

    @Override
    public tokenVO login(userDTO userDTO) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userDTO.getEmail());
        Users users = usersMapper.selectOne(queryWrapper);
        if (users == null) {
            throw new UserLostException(MessageConstant.USER_LOST);
        } else if (!userDTO.getEmail().equals(users.getEmail()) || !userDTO.getPassword().equals(users.getPassword())) {
            throw new UserLostException("用户名或密码错误");
        } else {
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.EMAIL, users.getUserId());

            SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

            String token = JwtUtil.createJWT(
                    secretKey,
                    jwtProperties.getAdminTtl(),
                    claims);

            tokenVO tokenVo = tokenVO.builder().id(users.getUserId())
                    .token(token)
                    .email(users.getEmail())
                    .build();
            return tokenVo;
        }


    }

}