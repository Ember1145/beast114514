package com.lts.consumer;

import com.lts.constant.RabbitConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static com.lts.constant.RabbitConstant.BASE_DELIVER_QUEUE;
import static com.lts.constant.RabbitConstant.TOPIC_EXCHANGE_NAME;

@Component
@RequiredArgsConstructor
public class DeliverConsumer {
    private final StringRedisTemplate stringRedisTemplate;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value =  BASE_DELIVER_QUEUE, durable = "true"),
                    exchange = @Exchange(value = RabbitConstant.DIRECT_EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
                    key = "baseDeliver"
            )
    )
    public void receiveMessage(Long tweetId) throws Exception {
        stringRedisTemplate.opsForZSet().addIfAbsent("baseDeliver:",tweetId.toString(),System.currentTimeMillis());
    }
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "picture.queue", durable = "true"),
                    exchange = @Exchange(value =  TOPIC_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
                    key = "picture.#"
            )
    )
    public void picturePart(Long tweetId) throws Exception {
        stringRedisTemplate.opsForZSet().addIfAbsent("pictureDeliver:",tweetId.toString(),System.currentTimeMillis());
    }
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "music.queue", durable = "true"),
                    exchange = @Exchange(value = TOPIC_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
                    key = "music.#"
            )
    )
    public void musicPart(Long tweetId) throws Exception {
        stringRedisTemplate.opsForZSet().addIfAbsent("musicDeliver:",tweetId.toString(),System.currentTimeMillis());
    }
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitConstant.INDIVIDUAL_DELIVER_QUEUE, durable = "true"),
                    exchange = @Exchange(value = RabbitConstant.DIRECT_EXCHANGE_NAME, type = ExchangeTypes.DIRECT),
                    key = "individualDeliver"
            )
    )
    public void receiveIndividualMessage(Message message) throws Exception {

    }
}
