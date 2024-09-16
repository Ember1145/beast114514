package com.lts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lts.domain.po.Tweets;
import com.lts.domain.vo.TweetStatusVO;
import com.lts.domain.vo.TweetVO;
import com.lts.mapper.TweetsMapper;
import com.lts.service.ITweetsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-04-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TweetsServiceImpl extends ServiceImpl<TweetsMapper, Tweets> implements ITweetsService {
    private final TweetsMapper tweetsMapper;

    @Override
    public List<TweetVO> tweetSearch(String qu) {
        List<TweetVO> tweetVO = tweetsMapper.tweetSearch(qu);
        return tweetVO;
    }
    @Override
    public IPage<TweetVO> findCommentsByTweetId(Long tweetId, Integer current, Integer size) {
        Page<TweetVO> page = new Page<>(current, size);
        return tweetsMapper.selectCommentsByTweetId(page, tweetId);
    }

    @Override
    public boolean hasFocusReply(Long userId, Long tweetId) {
        return tweetsMapper.hasFocusReply(userId,tweetId);
    }

    @Override
    public TweetVO findEarliestReplyByParentId(Long tweetId) {
        return tweetsMapper.findEarliestReplyByParentId(tweetId);
    }

    @Override
    public List<TweetVO> getTopChain(Long tweetId) {
        return tweetsMapper.getTopChain(tweetId);
    }

    @Override
    public TweetStatusVO queryStatus(Long tweetId, Long userId) {
        return tweetsMapper.queryStatus(tweetId,userId);
    }

    @Override
    public IPage<TweetVO> getLikeTweet(Long userId, Integer current, Integer size) {
        Page<TweetVO> page = new Page<>(current, size);
        return tweetsMapper.getLikedTweet(page,userId);
    }

    @Override
    public IPage<TweetVO> getShareAndMy(Long userId, Integer current, Integer size) {
        Page<TweetVO> page = new Page<>(current, size);
        return tweetsMapper.getShareAndMy(page,userId);
    }

    @Override
    public IPage<TweetVO> getBottomReplyTweet(Long userId, Integer current, Integer size) {
        Page<TweetVO> page = new Page<>(current, size);
        return tweetsMapper.getReplyTweet(page,userId);
    }

    @Override
    public TweetVO getTweetVOById(Long tweetId) {
        return tweetsMapper.getTweetVOById(tweetId);
    }

    @Override
    public IPage<TweetVO> getTopFold(Long userId, Integer current, int size) {
        Page<TweetVO> page = new Page<>(current, size);
        return tweetsMapper.getTopFold(page,userId);
    }

    @Override
    public TweetVO getTweetVOByRealParent(Long realParent) {
        return tweetsMapper.getTweetVOByRealParent(realParent);
    }

    @Override
    public List<TweetVO> getDeliverTweet(LambdaQueryWrapper<Tweets> range) {
         return tweetsMapper.getDeliverTweet(range);
    }


}
