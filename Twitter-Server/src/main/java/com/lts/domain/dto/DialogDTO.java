package com.lts.domain.dto;

import lombok.Data;

@Data
public class DialogDTO {
    private Long sendId;
    private Long targetId;
    private String content;
    private Integer type;
    private Long conversationId;
}
