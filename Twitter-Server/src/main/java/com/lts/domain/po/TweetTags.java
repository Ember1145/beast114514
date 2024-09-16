package com.lts.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2024-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "tweet_tags")
@ApiModel(value="TweetTags对象", description="")
@AllArgsConstructor(staticName = "of")
public class TweetTags implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "tweet_id", type = IdType.NONE)
    private Long tweetId;

    @TableField(typeHandler = FastjsonTypeHandler.class )
    private List<String> tag;


}
