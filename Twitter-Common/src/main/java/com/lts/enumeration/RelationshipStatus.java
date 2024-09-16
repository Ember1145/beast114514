package com.lts.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RelationshipStatus {

    NOT_FOLLOWING(0,"未关注"),
    FOLLOWING(1,"已关注"),
    MUTUAL(2,"互相关注"),
    BLOCKED(3,"拉黑");
   @EnumValue
   @JsonValue
    private final int value;
    private final String desc;

    RelationshipStatus(int value,String desc) {
        this.value = value;
        this.desc=desc;
    }
}