package com.lts.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
public class Message {

    private List<LikeMsgVO> likeMsgVOList;
    private List<List<LikeMsgVO>> likeAggregate;
    private MsgCountVO msgCountVO;
    private String type;
}
