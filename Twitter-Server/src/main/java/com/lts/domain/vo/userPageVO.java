package com.lts.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class userPageVO {
    private String avatarUrl;
    private String introduction;
    private String position;
    private String backUrl;
    private String username;
    private String emailCut;
    private Long userId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer followersCount;
    private Integer followingCount;
    private Integer tweetsCount;
    private Integer status;
}
