package com.lts.domain.dto;

import lombok.Data;

@Data
public class UserHomeDTO {
    private String avatarUrl;
    private String introduction;
    private String position;
    private String backUrl;
    private String username;
    private String oldBackUrl;
    private String oldAvatarUrl;
}
