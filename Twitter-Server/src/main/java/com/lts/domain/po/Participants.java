package com.lts.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2024-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("participants")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Participants implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "conversation_id", type = IdType.AUTO)
    private Long conversationId;

    @TableField("user_id")
    private Long userId;

    @TableField("joined_at")
    private LocalDateTime joinedAt;


}
