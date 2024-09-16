package com.lts.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lts.domain.po.Messages;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lts.domain.vo.dialogMsg.DialogVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-05-20
 */
public interface IMessagesService extends IService<Messages> {

    IPage<DialogVO> getFullMsg(Long conversationId, Integer current, Integer size);
}
