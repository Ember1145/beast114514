package com.lts.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class TweetDetailVO {
    private List<TweetVO> commentVOList;
   private List<List<TweetVO>> focusChains;
    private List<List<TweetVO>> topChain;
}
