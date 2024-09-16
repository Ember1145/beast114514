package com.lts.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class MsgCountVO {
    private String msgCount;
    private String type;
}
