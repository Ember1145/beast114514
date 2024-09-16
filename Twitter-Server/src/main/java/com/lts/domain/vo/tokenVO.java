package com.lts.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户登录返回的数据格式")
public class tokenVO implements Serializable {
    @ApiModelProperty("token")
    private String token;
    @ApiModelProperty("emailCut")
    private String emailCut;
    @ApiModelProperty("userid")
    private Long userId;
    @ApiModelProperty("avatarUrl")
    private String avatarUrl;
    @ApiModelProperty("username")
    private String username;
}
