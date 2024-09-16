package com.lts.service.impl;

import com.lts.domain.po.Likes;
import com.lts.domain.vo.TweetVO;
import com.lts.mapper.LikesMapper;
import com.lts.service.ILikesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-04-29
 */
@Service
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes> implements ILikesService {

}
