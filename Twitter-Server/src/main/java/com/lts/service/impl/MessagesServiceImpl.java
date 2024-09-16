package com.lts.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lts.domain.po.Messages;
import com.lts.domain.vo.dialogMsg.DialogVO;
import com.lts.mapper.MessagesMapper;
import com.lts.service.IMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2024-05-20
 */
@Service
@RequiredArgsConstructor
public class MessagesServiceImpl extends ServiceImpl<MessagesMapper, Messages> implements IMessagesService {
      private final MessagesMapper messagesMapper;
    @Override
    public IPage<DialogVO> getFullMsg(Long conversationId, Integer current, Integer size) {
        Page<DialogVO> page = new Page<>(current, size);
        return messagesMapper.getFullMsg(conversationId,page);
    }
}
