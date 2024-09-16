package com.lts.domain.vo;

import com.lts.domain.po.Tweets;
import com.lts.domain.po.Users;
import lombok.Data;

import java.util.List;
@Data
public class SearchVO {
    private List<Users> users; // 用户列表
    private List<TweetVO> tweets;
}
