package com.lts.service.impl;

import com.lts.domain.po.Tweets;
import com.lts.mapper.TweetsMapper;
import com.lts.service.ITweetsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-04-07
 */
@Service
public class TweetsServiceImpl extends ServiceImpl<TweetsMapper, Tweets> implements ITweetsService {

}
