package com.lts.domain.dto.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor(staticName = "of")
@Data
public class ParantIdAndTweetIdDTO implements Serializable {
    private Long parentId;
    private Long tweetId;
}
