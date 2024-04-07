package com.lts.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2024-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tweets")
@ApiModel(value="Tweets对象", description="")
public class Tweets implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "tweet_id", type = IdType.AUTO)
    private Integer tweetId;

    private Integer userId;

    private String content;

    private LocalDateTime createdAt;

    private String image;


}
