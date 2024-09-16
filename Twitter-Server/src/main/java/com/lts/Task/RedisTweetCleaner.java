package com.lts.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class RedisTweetCleaner {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 假设继续使用 userNotificationIndex
    private static final String USER_NOTIFICATION_INDEX = "userNotificationIndex";

    // 每小时执行一次清理任务
    @Scheduled(cron = "0 0 2 * * ?")
    public void removeExpiredTweets() {
        double now = System.currentTimeMillis();
        double minScore = 0; // 最小分数设为 0
        double maxScore = now - (60 * 60 * 1000*24*3); // 设置为1小时前的时间戳

        // 删除USER_NOTIFICATION_INDEX有序集合中超过1小时未更新的tweetId
        stringRedisTemplate.opsForZSet().removeRangeByScore(USER_NOTIFICATION_INDEX, minScore, maxScore);
        System.out.println("过期tweets已被清理");
    }
}
