package com.lts.domain.dto;

import lombok.Data;

@Data
public class LikeDTO {
    private Long tweetId;
    private Integer liked;
    private Long userId;
}
