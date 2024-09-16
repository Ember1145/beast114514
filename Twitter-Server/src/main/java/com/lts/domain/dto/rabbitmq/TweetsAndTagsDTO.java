package com.lts.domain.dto.rabbitmq;

import com.lts.domain.po.TweetTags;
import com.lts.domain.po.Tweets;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class TweetsAndTagsDTO implements Serializable {

    private Long tweetId;
    private Long parentId;
    private List<String> tag;
    private Long userId;
}
