package com.lts.service.impl;

import com.lts.domain.po.Participants;
import com.lts.mapper.ConversationsMapper;
import com.lts.mapper.ParticipantsMapper;
import com.lts.service.IParticipantsService;
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
public class ParticipantsServiceImpl extends ServiceImpl<ParticipantsMapper, Participants> implements IParticipantsService {
private final ConversationsMapper conversationsMapper;
    @Override
    public Long searchConversationId(Long sendId, Long targetId) {
        return conversationsMapper.searchConversationId(sendId,targetId);
    }
}
