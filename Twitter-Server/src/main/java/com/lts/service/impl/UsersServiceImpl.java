package com.lts.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lts.Properties.JwtProperties;
import com.lts.constant.JwtClaimsConstant;
import com.lts.constant.MessageConstant;
import com.lts.domain.dto.userDTO;
import com.lts.domain.po.UserStatistics;
import com.lts.domain.po.Users;
import com.lts.domain.vo.tokenVO;
import com.lts.domain.vo.userPageVO;
import com.lts.exception.UserExistsException;
import com.lts.exception.UserLostException;
import com.lts.mapper.UserStatisticsMapper;
import com.lts.mapper.UsersMapper;
import com.lts.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lts.utils.JwtSecretKeyUtil;
import com.lts.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

    private final UsersMapper usersMapper;
    private final UserStatisticsMapper userStatisticsMapper;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void userRegister(userDTO userDTO) {

        // 首先检查用户是否已经存在
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Users> eq = queryWrapper.eq(Users::getEmail, userDTO.getEmail()).eq(Users::getEmailCut, userDTO.getEmailCut());
        Users existingUser = usersMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            throw new UserExistsException(MessageConstant.ALREADY_EXISTS);
        } else {
            // 如果用户不存在，则将 DTO 转换为实体类并插入数据库
            Users newUser = BeanUtil.copyProperties(userDTO, Users.class);
            newUser.setCreatedAt(LocalDateTime.now());
            usersMapper.insert(newUser);
            UserStatistics userStatistics = new UserStatistics();
            userStatistics.setUserId(newUser.getUserId());
            userStatisticsMapper.insert(userStatistics);
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
            claims.put(JwtClaimsConstant.USERID, users.getUserId());


            SecretKey secretKey = null;
            try {
                secretKey = JwtSecretKeyUtil.generateSecretKey(jwtProperties.getAdminSecretKey(), "salt".getBytes(), 65536, 256);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
            String token = JwtUtil.createJWT(
                 secretKey,
                    jwtProperties.getAdminTtl(),
                    claims);

            tokenVO tokenVo = tokenVO.builder()
                    .token(token)
                    .emailCut(users.getEmailCut())
                    .userId(users.getUserId())
                    .avatarUrl(users.getAvatarUrl())
                    .username(users.getUsername())
                    .build();
            return tokenVo;
        }


    }
    public List<Users> findUserByEmailOrUsername(String qu) {
                LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.like(Users::getEmailCut, qu)
                .or()
                .like(Users::getUsername, qu);
        return usersMapper.selectList(queryWrapper).stream().distinct().collect(Collectors.toList());
    }

    @Override
    public userPageVO queryUser(String emailCut, Long userId) {
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getEmailCut,emailCut);
        Users users = usersMapper.selectOne(queryWrapper);
        return usersMapper.queryUser(emailCut,userId,users.getUserId());
    }
}