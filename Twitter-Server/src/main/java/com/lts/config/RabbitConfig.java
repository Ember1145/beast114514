package com.lts.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.lts.constant.RabbitConstant.TOPIC_EXCHANGE_NAME;

//@Configuration
public class RabbitConfig {
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }
    @Bean
    public Queue userNotificationQueue() {
        return new Queue("user.notifications.queue", true); // durable queue
    }

    @Bean
    public Binding binding(TopicExchange topicExchange, Queue userNotificationQueue) {
        return BindingBuilder.bind(userNotificationQueue).to(topicExchange).with("user.*.#");
    }
}