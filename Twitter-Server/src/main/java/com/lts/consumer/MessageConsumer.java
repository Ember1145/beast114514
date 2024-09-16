package com.lts.consumer;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lts.constant.RabbitConstant;
import com.lts.domain.dto.rabbitmq.ParantIdAndTweetIdDTO;
import com.lts.domain.dto.rabbitmq.TweetsAndTagsDTO;
import com.lts.domain.po.TweetInteractions;
import com.lts.domain.po.TweetTags;
import com.lts.domain.po.Tweets;
import com.lts.domain.po.UserStatistics;
import com.lts.service.*;
import com.lts.utils.WebSocketServerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessageConsumer {
    private final ITweetsService iTweetsService;
    private final ITweetTagsService iTweetTagsService;
    private final MessageService messageService;
    private final ITweetInteractionsService iTweetInteractionsService;
    private final IUserStatisticsService iUserStatisticsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "tweetCreate.queue", durable = "true"),
            exchange = @Exchange(value = RabbitConstant.DIRECT_EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
            key = "tweetCreate"
    ))
    public void handleTweetCreation(TweetsAndTagsDTO tweetsAndTagsDTO) {
        TweetTags tweetTags = TweetTags.of(tweetsAndTagsDTO.getTweetId(), tweetsAndTagsDTO.getTag());
        if(tweetsAndTagsDTO.getTag()==null){
            messageService.sendBaseDeliver(tweetsAndTagsDTO.getTweetId());
        }else {
            for (String tag:tweetsAndTagsDTO.getTag()){
                if(tag.equals("picture")) {
                    messageService.sendPicture(tweetsAndTagsDTO.getTweetId());
                }else if(tag.equals("music")) {
                    messageService.sendMusic(tweetsAndTagsDTO.getTweetId());
                }
            }
        }
        messageService.sendTweetTag(tweetTags);
        messageService.sendBaseDeliver(tweetsAndTagsDTO.getTweetId());
        ParantIdAndTweetIdDTO parantIdAndTweetIdDTO = ParantIdAndTweetIdDTO
                .of(tweetsAndTagsDTO.getParentId(), tweetsAndTagsDTO.getTweetId());
        messageService.sendCommentCountsUpdate(parantIdAndTweetIdDTO);
        messageService.sendCountsUpdate(tweetsAndTagsDTO.getUserId());
    }
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitConstant.INDIVIDUAL_DELIVER_QUEUE, durable = "true"),
                    exchange = @Exchange(value = RabbitConstant.DIRECT_EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
                    key = "tweetTags"
            )
    )
    public void receiveTweetTags(TweetTags tweetTags) throws Exception {
        iTweetTagsService.saveOrUpdate(tweetTags);
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "updateCommentCounts.queue", durable = "true"),
            exchange = @Exchange(value = RabbitConstant.DIRECT_EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
            key = "commentCountsUpdate"
    ))
    public void handleCommentCountsUpdate(ParantIdAndTweetIdDTO parantIdAndTweetIdDTO) {
        // 执行评论数更新
        TweetInteractions tweetInteractions = new TweetInteractions();
        tweetInteractions.setTweetId(parantIdAndTweetIdDTO.getTweetId());
        iTweetInteractionsService.save(tweetInteractions);
        updateCommentCounts(parantIdAndTweetIdDTO.getParentId());
    }
    public boolean updateCommentCounts(Long tweetId) {
        while (tweetId != null) {
            LambdaQueryWrapper<Tweets> queryWrapper = new LambdaQueryWrapper<Tweets>()
                    .eq(Tweets::getTweetId,tweetId)
                    .select(Tweets::getParentId);
            Map<String, Object> map = iTweetsService.getMap(queryWrapper);
            LambdaUpdateWrapper<TweetInteractions> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TweetInteractions::getTweetId, tweetId)
                    .setSql("comment_count = comment_count + 1");
            iTweetInteractionsService.update(updateWrapper);
            if (map != null && map.containsKey("parent_id")) {
                tweetId = (Long)map.get("parent_id");
            } else {
                // 如果 map 为 null 或者不存在 parent_id，中断循环
                break;
            }
        }
        return true;
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "tweet.counts.queue", durable = "true"),
            exchange = @Exchange(value = RabbitConstant.DIRECT_EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
            key = "countsUpdate"
    ))
    public void handleCountsUpdate(Long userId) {
        LambdaUpdateWrapper<UserStatistics> updateWrapper = new LambdaUpdateWrapper<UserStatistics>()
                .eq(UserStatistics::getUserId,userId)
                .setSql("tweets_count = tweets_count + 1");
        iUserStatisticsService.update(updateWrapper);
    }
}
