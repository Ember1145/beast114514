package com.lts.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TweetStatusVO {
    private Integer liked;
    private Integer shared;
    private Long commentCount;
    private Long shareCount;
    private Long likeCount;
}
