package com.lts.mapper;

import com.lts.domain.po.Conversations;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2024-05-20
 */
public interface ConversationsMapper extends BaseMapper<Conversations> {
    @Select("SELECT c.id FROM conversations c JOIN participants p1 ON c.id=p1.conversation_id " +
            "JOIN participants p2 ON c.id=p2.conversation_id WHERE p1.user_id=#{sendId} " +
            "AND p2.user_id=#{targetId} AND c.group_name IS NULL")
    Long searchConversationId(Long sendId, Long targetId);
}
