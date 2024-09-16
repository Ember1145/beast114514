package com.lts.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class TweetVO {
    private String avatarUrl;
    private Long tweetId;

    private Long userId;

    private String content;
    private List<String> media;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Long parentId;
    private String username;
    private String emailCut;
    private Long realParent;
}
