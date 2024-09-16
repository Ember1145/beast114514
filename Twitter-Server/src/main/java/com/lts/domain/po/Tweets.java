package com.lts.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@Accessors(chain = true)
@TableName(value = "tweets",autoResultMap = true)
@ApiModel(value="Tweets对象", description="推文")
@AllArgsConstructor
@NoArgsConstructor
public class Tweets implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "tweet_id", type = IdType.AUTO)
    private Long tweetId;

    private Long userId;

    private String content;

    private LocalDateTime createdAt;

    @TableField(typeHandler = FastjsonTypeHandler.class )
    private List<String> media;

    @TableField("parent_id")
    private Long parentId;
    private Long realParent;

}


