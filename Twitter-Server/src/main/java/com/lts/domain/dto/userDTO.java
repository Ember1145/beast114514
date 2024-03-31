package com.lts.domain.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class userDTO implements Serializable {
    private String username;

    private String avatarUrl;

    private String introduction;
}
