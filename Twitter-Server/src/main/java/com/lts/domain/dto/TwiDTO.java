package com.lts.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class TwiDTO {
    private List<String> media;
    private String content;
    private Long parentId;
    private Long realParent;
    private List<String> tag;
}
