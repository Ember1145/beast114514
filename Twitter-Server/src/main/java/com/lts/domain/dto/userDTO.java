package com.lts.domain.dto;

import lombok.Data;

@Data
public class userDTO {
    private String username ;
    private String password;
    private String avatarUrl;
    private String introduction;
    private String email;
}
