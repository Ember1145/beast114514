package com.lts.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lts.domain.po.Tweets;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lts.domain.vo.TweetStatusVO;
import com.lts.domain.vo.TweetVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Set;


public interface TweetsMapper extends BaseMapper<Tweets> {
    @Select("SELECT u.username, u.avatar_url, u.email_cut, t.* FROM users u,tweets t where u.user_id = t.user_id and t.content LIKE CONCAT('%', #{qu}, '%')")
    @Result(property = "media", column = "media",
                    typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    List<TweetVO> tweetSearch(String qu);
    IPage<TweetVO> selectCommentsByTweetId(Page<?> page,@Param("tweetId") Long tweetId);

    boolean hasFocusReply(Long userId,Long tweetId);

    @Select("SELECT child.*,u.avatar_url,u.username,u.email_cut FROM tweets AS child " +
            " JOIN tweets AS parent ON child.parent_id = parent.tweet_id "
            +"JOIN users AS u ON child.user_id = u.user_id"+
            " WHERE child.parent_id = #{tweetId}"+
            " ORDER BY child.created_at ASC LIMIT 1")
    @Result(property = "media", column = "media",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    TweetVO findEarliestReplyByParentId(@Param("tweetId")Long tweetId);
    @Result(property = "media", column = "media",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    List<TweetVO> getTopChain(Long tweetId);
    @Select("SELECT i.like_count, i.comment_count, i.share_count, l.liked, s.shared " +
            "FROM tweet_interactions i " +
            "LEFT JOIN shares s ON s.tweet_id = i.tweet_id AND s.user_id = #{userId} " +
            "LEFT JOIN likes l ON l.tweet_id = i.tweet_id AND l.user_id = #{userId} " +
            "WHERE i.tweet_id = #{tweetId}")
    TweetStatusVO queryStatus(@Param("tweetId") Long tweetId,@Param("userId") Long userId);
@Select("SELECT t.*,u.avatar_url,u.username,u.email_cut FROM tweets t " +
        "JOIN users u ON t.user_id=u.user_id JOIN likes l ON t.tweet_id=l.tweet_id " +
        "WHERE l.user_id=#{userId} and l.liked=1 order by l.like_time desc")
@Result(property = "media", column = "media",
        typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    IPage<TweetVO> getLikedTweet(Page<TweetVO> page, Long userId);
    @Select("SELECT * FROM (" +
            "SELECT t.*, u.avatar_url, u.username, u.email_cut, s.share_time AS sort_time FROM tweets t " +
            "JOIN users u ON t.user_id = u.user_id " +
            "JOIN shares s ON t.tweet_id = s.tweet_id " +
            "WHERE s.user_id=#{userId} and s.shared=1 " +
            "UNION " +
            "SELECT t.*, u.avatar_url, u.username, u.email_cut, t.created_at AS sort_time FROM tweets t " +
            "JOIN users u ON t.user_id = u.user_id " +
            "WHERE t.parent_id IS NULL and t.user_id=#{userId}" +
            ") AS combined_tweets " +
            "ORDER BY combined_tweets.sort_time DESC")
    @Result(property = "media", column = "media",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    IPage<TweetVO> getShareAndMy(Page<TweetVO> page, Long userId);
    @Select("SELECT parent.*, u.avatar_url, u.username, u.email_cut FROM tweets AS child " +
            "RIGHT JOIN tweets AS parent ON child.parent_id = parent.tweet_id " +
            "JOIN users AS u ON parent.user_id = u.user_id " +
            "WHERE parent.user_id = #{userId} AND child.tweet_id IS NULL AND parent.parent_id IS NOT NULL " +
            "ORDER BY parent.created_at DESC")
    @Result(property = "media", column = "media",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    IPage<TweetVO> getReplyTweet(Page<TweetVO> page, Long userId);

    @Select("SELECT u.username, u.avatar_url, u.email_cut, t.* FROM tweets t JOIN users u ON t.user_id=u.user_id " +
            "where t.tweet_id=#{tweetId}")
    @Result(property = "media", column = "media",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    TweetVO getTweetVOById(Long tweetId);
    @Select("SELECT t.*, u.avatar_url, u.username, u.email_cut FROM tweets t " +
            "JOIN users AS u ON t.user_id = u.user_id " +
            "WHERE t.user_id = #{userId} AND t.parent_id is not null " +
            "ORDER BY t.created_at DESC")
    @Result(property = "media", column = "media",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    IPage<TweetVO> getTopFold(Page<TweetVO> page, Long userId);
    @Select("SELECT u.username, u.avatar_url, u.email_cut, t.* FROM tweets t JOIN users u ON t.user_id=u.user_id " +
            "where t.tweet_id=#{realParent}")
    @Result(property = "media", column = "media",
            typeHandler = com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler.class)
    TweetVO getTweetVOByRealParent(Long realParent);
    List<TweetVO> getDeliverTweet(@Param("ew") LambdaQueryWrapper<Tweets> in);
}
