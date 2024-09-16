package com.lts.domain.vo.dialogMsg;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class DialogVO {
    private Long conversationId;
    private Long messageId;
    private String username;
    private String avatarUrl;
    private String emailCut;
    private Long senderId;
    private Integer type;
    private String content;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;
}
