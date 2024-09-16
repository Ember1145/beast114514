package com.lts.mapper;


import com.lts.domain.po.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lts.domain.vo.userPageVO;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2024-03-29
 */
public interface UsersMapper extends BaseMapper<Users> {
    userPageVO queryUser(String emailCut, Long followerId, Long userId);
}
