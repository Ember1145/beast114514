package com.lts.controller.Twi;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lts.context.userContext;
import com.lts.domain.dto.LikeDTO;
import com.lts.domain.po.Likes;
import com.lts.domain.po.TweetInteractions;
import com.lts.domain.po.Tweets;
import com.lts.domain.po.Users;
import com.lts.domain.vo.LikeMsgVO;
import com.lts.domain.vo.MsgCountVO;
import com.lts.domain.vo.OnlineLikeMsgVO;
import com.lts.domain.vo.TweetVO;
import com.lts.result.Result;
import com.lts.service.ILikesService;
import com.lts.service.ITweetInteractionsService;
import com.lts.service.ITweetsService;
import com.lts.service.IUsersService;
import com.lts.utils.WebSocketServerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final ILikesService iLikesService;
    private final IUsersService iUsersService;
    private final ITweetInteractionsService iTweetInteractionsService;
    private final ITweetsService iTweetsService;
    private final StringRedisTemplate stringRedisTemplate;

    @PostMapping("/updateLike")
    public Result updateLike(@RequestBody LikeDTO likeDTO) throws Exception {
        Long userId = userContext.getUserId();
        Long userTarget = likeDTO.getUserId();
        boolean success = updateOrSaveLikeStatus(userId, likeDTO.getTweetId(), likeDTO.getLiked(),userTarget);
        return Result.success("更新状态成功");
    }
    public boolean updateOrSaveLikeStatus(Long userId, Long tweetId, Integer liked,Long userTarget) throws Exception {
        // 创建查询条件
        if(liked.equals(0)){
            LambdaUpdateWrapper<Likes> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            LambdaUpdateWrapper<Likes> set = lambdaUpdateWrapper
                    .eq(Likes::getUserId, userId)
                    .eq(Likes::getTweetId, tweetId)
                    .set(Likes::getLiked,liked);
            iLikesService.update(set);
            LambdaUpdateWrapper<TweetInteractions> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper1.eq(TweetInteractions::getTweetId,tweetId)
                    .setSql("like_count = like_count - 1");
            iTweetInteractionsService.update(lambdaUpdateWrapper1);
            return iLikesService.update(set);
        }
        else {
            QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("tweet_id", tweetId);
            // 尝试查找记录
            Likes existingLike = iLikesService.getOne(queryWrapper);
            LambdaUpdateWrapper<TweetInteractions> lambdaUpdateWrapper2 = new LambdaUpdateWrapper<TweetInteractions>()
                    .eq(TweetInteractions::getTweetId,tweetId)
                    .setSql("like_count = like_count + 1");
            iTweetInteractionsService.update(lambdaUpdateWrapper2);
            Users users = iUsersService.getById(userId);
            TweetVO tweetVOById = iTweetsService.getTweetVOById(tweetId);
            LikeMsgVO likeMsg = LikeMsgVO.builder()
                    .avatarUrl(users.getAvatarUrl())
                    .emailCut(users.getEmailCut())
                    .username(users.getUsername())
                    .tweet(tweetVOById)
                    .createdAt(LocalDateTime.now())
                    .folded(false).build();

            String likeMsgVO = JSON.toJSONString(likeMsg);
            String userNotificationIndex = "user:" + userTarget + ":like_notifications";
            // 检查tweetId是否已经在有序集合中
                stringRedisTemplate.opsForZSet().add(userNotificationIndex, tweetId.toString(),System.currentTimeMillis());
            String detailKey = "tweet:" + tweetId + ":likes:details";
            String messageCountKey = "user:messageCount:"+userTarget;
            //查看这个推特id的likeMsgVo有值吗,有则有分数
            // 检查记录是否存在
            Double score1 = stringRedisTemplate.opsForZSet().score(detailKey, likeMsgVO);
            long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            //确保不是自己点自己,往目标用户添加点赞通知
            if(!userTarget.equals(userId)) {
                stringRedisTemplate.opsForZSet().addIfAbsent(detailKey, likeMsgVO, timestamp);
            }
            Long count = stringRedisTemplate.opsForValue().increment(messageCountKey);
            MsgCountVO count1 = MsgCountVO.of(String.valueOf(count) , "count");
            OnlineLikeMsgVO like = OnlineLikeMsgVO.of(likeMsg, "like");
            if (WebSocketServerUtil.isUserOnline(userTarget)&&score1==null&&!userTarget.equals(userId)) {
                WebSocketServerUtil.sendInfo(JSON.toJSONString(like), userTarget);
                WebSocketServerUtil.sendInfo(JSON.toJSONString(count1), userTarget);
            }
            // 如果存在记录，则更新点赞状态
            if (existingLike != null) {
                existingLike.setLiked(liked);
                return iLikesService.updateById(existingLike);
            } else {
                // 如果不存在记录，则添加新的点赞记录
                Likes newLike = new Likes();
                newLike.setUserId(userId);
                newLike.setTweetId(tweetId);
                newLike.setLiked(liked);
                return iLikesService.save(newLike);
            }

        }
    }
    @GetMapping("/{emailCut}/likes")
    public Result<List<TweetVO>> getLikeTweet(@PathVariable String emailCut, @RequestParam Integer current){
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getEmailCut,emailCut)
                .select(Users::getUserId);
        Map<String, Object> map = iUsersService.getMap(queryWrapper);
        IPage<TweetVO> like = iTweetsService.getLikeTweet((Long) map.get("user_id"),current,10);
        List<TweetVO> likeList = like.getRecords();
        return Result.success(likeList);
    }

}
