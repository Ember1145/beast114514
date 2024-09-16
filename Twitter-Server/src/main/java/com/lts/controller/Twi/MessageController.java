package com.lts.controller.Twi;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lts.context.userContext;
import com.lts.domain.po.TweetInteractions;
import com.lts.domain.vo.LikeMsgVO;
import com.lts.domain.vo.Message;
import com.lts.domain.vo.MsgCountVO;
import com.lts.domain.vo.dialogMsg.AllUnreadCountVO;
import com.lts.result.Result;
import com.lts.service.ITweetInteractionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final StringRedisTemplate redisTemplate;
    private final ITweetInteractionsService iTweetInteractionsService;
    private final StringRedisTemplate stringRedisTemplate;
    private final int size = 10;
    @GetMapping("/message")
    public Result<Message> getUserNotifications(@RequestParam Integer current) {
        Long userId = userContext.getUserId();
        int start = (current - 1) * size;
        int end = start + size - 1;
        String userNotificationIndex = "user:" + userId + ":like_notifications";
        Set<String> tweetIds = redisTemplate.opsForZSet().reverseRange(userNotificationIndex, start, end);
        if(tweetIds==null){
            return Result.success();
        }
        List<LikeMsgVO> notifications = new ArrayList<>();
        List<List<LikeMsgVO>> likeAggregate=new ArrayList<>();
        for (String tweetIdStr : tweetIds) {
            Long tweetId = Long.parseLong(tweetIdStr); // 直接使用 Long.parseLong 来转换字符串为 long
            String detailKey = "tweet:" + tweetId + ":likes:details";
            Set<String> likeDetailStrings = redisTemplate.opsForZSet().reverseRange(detailKey, 0, -1);
            List<LikeMsgVO> updatedLikesAggregate = likeDetailStrings.stream()
                    .map(item ->{ LikeMsgVO likeMsg = JSON.parseObject(item, LikeMsgVO.class);
            Double score = redisTemplate.opsForZSet().score(detailKey, item);
            if (score != null) {
                // 将 score 从 double 转换成需要的类型，例如 LocalDateTime
                LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(score.longValue()), ZoneId.systemDefault());
                likeMsg.setCreatedAt(createdAt);
            }
             return likeMsg;
                     }).toList();

            if (likeDetailStrings.size() > 2) {
                // 只处理前两个元素，并设置 folded 为 true
                LambdaQueryWrapper<TweetInteractions> queryWrapper = new LambdaQueryWrapper<>();
                LambdaQueryWrapper<TweetInteractions> eq = queryWrapper.eq(TweetInteractions::getTweetId, tweetId)
                        .select(TweetInteractions::getLikeCount);
                Optional<TweetInteractions> oneOpt = iTweetInteractionsService.getOneOpt(eq);
                    TweetInteractions tweetInteractions = oneOpt.get(); // 如果值存在，则获取
                    Long likeCount = tweetInteractions.getLikeCount(); // 从实体对象中获取点赞计数
                List<LikeMsgVO> updatedLikes = likeDetailStrings.stream()
                        .limit(2)
                        .map(item -> {
                            LikeMsgVO likeMsg = JSON.parseObject(item, LikeMsgVO.class);
                            likeMsg.setFolded(true); // 设置折叠标志
                            likeMsg.setLikeCount(likeCount); // 设置点赞数量
                            Double score = redisTemplate.opsForZSet().score(detailKey, item);
                            if (score != null) {
                                // 将 score 从 double 转换成需要的类型，例如 LocalDateTime
                                LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(score.longValue()), ZoneId.systemDefault());
                                likeMsg.setCreatedAt(createdAt);
                            }
                            return likeMsg;
                        })
                        .collect(Collectors.toList());
                // 获取分数最高的一个元素
                Set<ZSetOperations.TypedTuple<String>> resultSet = redisTemplate.opsForZSet().reverseRangeWithScores(detailKey, 0, 0);

                    ZSetOperations.TypedTuple<String> lastElement = resultSet.iterator().next();
                    Double scoreOfLastElement = lastElement.getScore();
                    // 最后删除整个ZSet的所有元素
                    redisTemplate.opsForZSet().removeRange(detailKey, 0, -1);
                // 将处理后的元素重新添加
                //第三个参数为获取最高分数
                for (LikeMsgVO likeMsg : updatedLikes) {
                    redisTemplate.opsForZSet().add(detailKey, JSON.toJSONString(likeMsg),scoreOfLastElement );
                }
                likeAggregate.add(updatedLikes);
                notifications.addAll(updatedLikesAggregate);
            } else {
                // 当点赞详情不超过两个时，直接添加到 notifications
                List<LikeMsgVO> likeDetails = likeDetailStrings.stream()
                        .map(item -> {
                            LikeMsgVO likeMsg = JSON.parseObject(item, LikeMsgVO.class);
                            Double score = redisTemplate.opsForZSet().score(detailKey, item);
                            if (score != null) {
                                // 将 score 从 double 转换成需要的类型，例如 LocalDateTime
                                LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(score.longValue()), ZoneId.systemDefault());
                                likeMsg.setCreatedAt(createdAt);
                            }
                            return likeMsg;
                        }).collect(Collectors.toList());
                if(!likeDetails.isEmpty()&&likeDetails.get(0).getFolded()){
                    likeAggregate.add(likeDetails);
                }else {
                    notifications.addAll(likeDetails);
                }
            }
        }

        Message like = Message.builder()
                .type("like").build();
        if(!likeAggregate.isEmpty()){
            like.setLikeAggregate(likeAggregate);
        }
        if(!notifications.isEmpty()){
            like.setLikeMsgVOList(notifications);
        }
        return Result.success(like);
    }
   @PatchMapping("/message/clear")
    public Result clearCount(){
    Long userId = userContext.getUserId();
    String messageCountKey = "user:messageCount:"+userId;
    redisTemplate.opsForValue().set(messageCountKey,"0");
    return Result.success();
  }
    @PatchMapping("/message/clearDialogCount")
    public Result clearHomeDialogCount(){
        Long userId = userContext.getUserId();
        String dialogCount="dialogUnreadCount:"+userId;
        stringRedisTemplate.opsForValue().set(dialogCount,"0");
        return Result.success();
    }
  @GetMapping("/homeUnreadCount")
    public Result<AllUnreadCountVO> getHomeUnreadCount(){
      Long userId = userContext.getUserId();
      String messageCountKey = "user:messageCount:"+userId;
      String dialogCount="dialogUnreadCount:"+userId;
      String count = stringRedisTemplate.opsForValue().get(messageCountKey);
      String s = stringRedisTemplate.opsForValue().get(dialogCount);
      AllUnreadCountVO allUnreadCountVO = AllUnreadCountVO.of(count, s);
      return Result.success(allUnreadCountVO);
  }
}
