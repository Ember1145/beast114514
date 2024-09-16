package com.lts.domain.vo.dialogMsg;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class AllUnreadCountVO {
    private String messageCount;
    private String dialogCount;
}
