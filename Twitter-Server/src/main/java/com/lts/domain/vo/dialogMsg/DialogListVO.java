package com.lts.domain.vo.dialogMsg;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DialogListVO {
    private Long conversationId;
    private String lastMsg;
    private String username;
    private String avatarUrl;
    private String emailCut;
    private Integer type;
    private String content;
    private Integer unreadCount;
}
