package com.lts.service;


import com.lts.domain.dto.rabbitmq.ParantIdAndTweetIdDTO;
import com.lts.domain.dto.rabbitmq.TweetsAndTagsDTO;
import com.lts.domain.po.TweetTags;
import com.lts.domain.po.Tweets;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.lts.constant.RabbitConstant.DIRECT_EXCHANGE_NAME;
import static com.lts.constant.RabbitConstant.TOPIC_EXCHANGE_NAME;

@Service
@RequiredArgsConstructor
    public class MessageService {
        private final RabbitTemplate rabbitTemplate;
        public void sendBaseDeliver(Long message) {
            rabbitTemplate.convertAndSend(
                    DIRECT_EXCHANGE_NAME,
                    "baseDeliver",
                    message
            );
        }
    public void sendIndividualDeliver(String message) {
        rabbitTemplate.convertAndSend(
                DIRECT_EXCHANGE_NAME,
                "IndividualDeliver",
                message
        );
    }
    public void sendTweetCreation(TweetsAndTagsDTO tweetsAndTagsDTO) {
        rabbitTemplate.convertAndSend(
                DIRECT_EXCHANGE_NAME,
                "tweetCreate",
                tweetsAndTagsDTO
        );
    }
    public void sendTweetTag(TweetTags tweetTags) {
        rabbitTemplate.convertAndSend(
                DIRECT_EXCHANGE_NAME,
                "tweetTags",
                tweetTags
        );
    }
    public void sendCommentCountsUpdate(ParantIdAndTweetIdDTO parantIdAndTweetIdDTO) {
        rabbitTemplate.convertAndSend(
                DIRECT_EXCHANGE_NAME,
                "commentCountsUpdate",
                parantIdAndTweetIdDTO
        );
    }
    public void sendCountsUpdate(Long userId) {
        rabbitTemplate.convertAndSend(
                DIRECT_EXCHANGE_NAME,
                "countsUpdate",
               userId
        );
    }
    public void sendPicture(Long tweetId) {
        rabbitTemplate.convertAndSend(
                TOPIC_EXCHANGE_NAME,
                "picture.common",
                tweetId
        );
    }
    public void sendMusic(Long tweetId) {
        rabbitTemplate.convertAndSend(
                TOPIC_EXCHANGE_NAME,
                "music.common",
                tweetId
        );
    }
    }

