package com.lts.controller.user;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lts.constant.QuerySizeConstant;
import com.lts.context.userContext;
import com.lts.domain.dto.DialogDTO;
import com.lts.domain.po.Conversations;
import com.lts.domain.po.Messages;
import com.lts.domain.po.Participants;
import com.lts.domain.po.Users;
import com.lts.domain.vo.dialogMsg.*;
import com.lts.result.Result;
import com.lts.service.IConversationsService;
import com.lts.service.IMessagesService;
import com.lts.service.IParticipantsService;
import com.lts.service.IUsersService;
import com.lts.utils.WebSocketServerUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ConversationController {
    private final IConversationsService iConversationsService;
    private final IParticipantsService iParticipantsService;
    private final IMessagesService iMessagesService;
    private final StringRedisTemplate stringRedisTemplate;
    private final IUsersService iUsersService;
    @GetMapping("/getConversation")
    public Result<Long> getConversationId(Long sendId,Long targetId){
        Long isExist = iParticipantsService.searchConversationId(sendId,targetId);
        if(isExist!=null){
            return Result.success(isExist);
        }
        else {
            Conversations conversations = new Conversations();
            iConversationsService.save(conversations);
            Long newId=conversations.getId();
            Participants build = Participants.builder().conversationId(newId).userId(sendId).build();
            Participants build1 = Participants.builder().conversationId(newId).userId(targetId).build();
            iParticipantsService.save(build);
            iParticipantsService.save(build1);
            return Result.success(newId);
        }
    }
    @PatchMapping("/messages/clear")
    public Result clearCount(@RequestParam Long conversationId){
        Long userId = userContext.getUserId();
        String personalDetail = conversationId + ":detail:" + userId;
        stringRedisTemplate.opsForHash().put(personalDetail,"unreadCount","0");
        return Result.success();
    }

    @PostMapping("/send")
    public Result<Long> sendMsg(@RequestBody DialogDTO dialogDTO) {
        Long conversationId = dialogDTO.getConversationId();
        LambdaQueryWrapper<Participants> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participants::getConversationId, conversationId)
                .select(Participants::getUserId);  // 选择userId字段
        List<Participants> participants = iParticipantsService.list(queryWrapper);
                Messages build1 = Messages.builder()
                        .conversationId(conversationId)
                        .senderId(dialogDTO.getSendId())
                        .content(dialogDTO.getContent())
                        .type(dialogDTO.getType())
                        .build();
                iMessagesService.save(build1);
            DialogVO dialogVO = BeanUtil.copyProperties(build1, DialogVO.class);
            LambdaQueryWrapper<Users> queryWrapper1 = new LambdaQueryWrapper<Users>()
                    .eq(Users::getUserId, dialogDTO.getSendId())
                    .select(Users::getAvatarUrl, Users::getEmailCut, Users::getUsername);
            Map<String, Object> userMap = iUsersService.getMap(queryWrapper1);
            if (userMap != null) {
                dialogVO.setAvatarUrl((String) userMap.get("avatar_url"));
                dialogVO.setEmailCut((String) userMap.get("email_cut"));
                dialogVO.setUsername((String) userMap.get("username"));
            }
// 遍历所有参与者并通知，排除发送者自己的ID
                Long senderId = dialogDTO.getSendId();
                //更新每个被通知用户的列表与列表详情,还有对话详情
                participants.stream()
                        .map(Participants::getUserId) // 获取userId
                        .filter(userId -> !userId.equals(senderId)) // 排除发送者自己的ID
                        .forEach(targetId -> {
                            try {
                                updateDialogListAndDetail(targetId,dialogVO,conversationId);
                                informUser(targetId, conversationId,dialogVO);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                return Result.success(build1.getMessageId());

        }
    public void updateDialogListAndDetail(Long userId, DialogVO dialogVO, Long conversationId) {
        String key = "sendList:" + userId;
        String personalDetail = conversationId + ":detail:" + userId;
        String dialogCount="dialogUnreadCount:"+userId;
        stringRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(@NonNull RedisOperations operations) throws DataAccessException {
                operations.multi(); // 开始事务
                // 直接递增，即使键不存在也没关系，Redis会创建它
                operations.opsForHash().increment(personalDetail, "unreadCount", 1L);
                operations.opsForValue().increment(dialogCount);
                // 更新总列表
                operations.opsForZSet().add(key, personalDetail, System.currentTimeMillis());
                // 更新列表详情
                Map<String, String> hashValues = new HashMap<>();
                hashValues.put("avatarUrl", dialogVO.getAvatarUrl());
                hashValues.put("emailCut", dialogVO.getEmailCut());
                hashValues.put("username", dialogVO.getUsername());
                hashValues.put("lastMsg", dialogVO.getContent());
                hashValues.put("type", dialogVO.getType().toString());
                hashValues.put("conversationId", conversationId.toString());
                operations.opsForHash().putAll(personalDetail, hashValues);
                return operations.exec(); // 执行事务
            }
        });
    }

    public DialogListVO getDialogDetails(String personalDetail) {
        // 使用 Hash 操作获取所有详情字段和它们的值
        Map<Object, Object> detailFields = stringRedisTemplate.opsForHash().entries(personalDetail);
        // 创建 VO 对象并设置属性
        DialogListVO dialogListVO = new DialogListVO();
        dialogListVO.setAvatarUrl((String) detailFields.get("avatarUrl"));
        dialogListVO.setEmailCut((String) detailFields.get("emailCut"));
        dialogListVO.setUsername((String) detailFields.get("username"));
        dialogListVO.setLastMsg((String) detailFields.get("lastMsg"));
        dialogListVO.setType(Integer.parseInt((String) detailFields.get("type")));
        dialogListVO.setUnreadCount(Integer.parseInt((String) detailFields.get("unreadCount")));
        dialogListVO.setConversationId(Long.valueOf((String)detailFields.get("conversationId")));
        return dialogListVO;
    }
    public void informUser(Long targetId, Long conversationId, DialogVO dialogVO) throws Exception {
        if (WebSocketServerUtil.isUserOnline(targetId)&&!targetId.equals(dialogVO.getSenderId())) {
            OnlineDialogVO onlineMsg = OnlineDialogVO.of(dialogVO, "onlineMsg");
            String personalDetail=conversationId+":detail:"+targetId;
            String dialogCount="dialogUnreadCount:"+targetId;
            String s = stringRedisTemplate.opsForValue().get(dialogCount);
            DialogListVO dialogListVO = getDialogDetails(personalDetail);
            OnlineDialogListVO onlineList = OnlineDialogListVO.of(dialogListVO,s, "onlineList");
            WebSocketServerUtil.sendInfo(JSON.toJSONString(onlineList),targetId);
            WebSocketServerUtil.sendInfo(JSON.toJSONString(onlineMsg),targetId);
        }
    }
    @GetMapping("/messages/{conversationId}")
    public Result<List<DialogVO>> getChatMsg(@PathVariable Long conversationId,Integer current){
        IPage<DialogVO> msgPage= iMessagesService.getFullMsg(conversationId,current,QuerySizeConstant.SIZE);
        return Result.success(msgPage.getRecords());
    }
    @GetMapping("/messages")
    public Result<List<DialogListVO>> getSendList(Integer current){
        Long userId = userContext.getUserId();
        String key="sendList:"+userId;
        int start = ((current - 1) * QuerySizeConstant.SIZE);
        int end = start + QuerySizeConstant.SIZE - 1;
        Set<String> sendList= stringRedisTemplate.opsForZSet().range(key, start, end);
        if(sendList==null){
            return Result.success();
        }
        List<DialogListVO> list=new ArrayList<>();
        for(String item:sendList){
            DialogListVO dialogDetails = getDialogDetails(item);
            list.add(dialogDetails);
        }
        return Result.success(list);
    }
}
