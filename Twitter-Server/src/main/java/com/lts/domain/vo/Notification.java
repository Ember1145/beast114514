package com.lts.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private String senderName;  // 发送者名称
    private String senderAvatar;  // 发送者头像
    private String message;  // 消息内容
    private String tweetId;  // 推文的URL
}