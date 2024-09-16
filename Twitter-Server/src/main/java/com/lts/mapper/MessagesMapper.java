package com.lts.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lts.domain.po.Messages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lts.domain.vo.dialogMsg.DialogVO;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2024-05-20
 */
public interface MessagesMapper extends BaseMapper<Messages> {

    @Select("SELECT m.*,u.avatar_url,u.email_cut,u.username FROM messages m JOIN users u ON " +
            "m.sender_id=u.user_id  WHERE m.conversation_id=#{conversationId} order by m.sent_at desc ")
    IPage<DialogVO> getFullMsg(Long conversationId, Page<DialogVO> page);
}
