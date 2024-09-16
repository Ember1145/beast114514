package com.lts.service;

import com.lts.domain.po.Participants;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-05-20
 */
public interface IParticipantsService extends IService<Participants> {

    Long searchConversationId(Long sendId, Long targetId);
}
