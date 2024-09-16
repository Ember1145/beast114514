package com.lts.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lts.domain.po.Tweets;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lts.domain.vo.TweetStatusVO;
import com.lts.domain.vo.TweetVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-04-07
 */
public interface ITweetsService extends IService<Tweets> {

   List<TweetVO> tweetSearch(String qu);

    IPage<TweetVO> findCommentsByTweetId(@Param("tweetId") Long tweetId, @Param("current") Integer current, @Param("size") Integer size);

    boolean hasFocusReply(Long userId, Long tweetId);

    TweetVO findEarliestReplyByParentId(Long tweetId);

    List<TweetVO> getTopChain(Long parentId);

    TweetStatusVO queryStatus(Long tweetId,Long userId);

 IPage<TweetVO> getLikeTweet(Long userId, Integer current, Integer size);

 IPage<TweetVO> getShareAndMy(Long userId, Integer current, Integer size);

 IPage<TweetVO> getBottomReplyTweet(Long userId, Integer current, Integer size);

 TweetVO getTweetVOById(Long tweetId);

 IPage<TweetVO> getTopFold(Long userId, Integer current, int i);

 TweetVO getTweetVOByRealParent(Long realParent);

    List<TweetVO> getDeliverTweet(LambdaQueryWrapper<Tweets> range);
}
