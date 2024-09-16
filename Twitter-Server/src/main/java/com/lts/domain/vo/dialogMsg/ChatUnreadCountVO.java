package com.lts.domain.vo.dialogMsg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ChatUnreadCountVO {
    private Long conversationId;
    private String type;
    private String count;
}
