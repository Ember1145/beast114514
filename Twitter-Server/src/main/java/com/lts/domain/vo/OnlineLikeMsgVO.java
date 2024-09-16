package com.lts.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class OnlineLikeMsgVO {
    private LikeMsgVO likeMsgVO;
    private String type;
}
