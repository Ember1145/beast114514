package com.lts.controller.Twi;

import com.lts.domain.po.Users;
import com.lts.domain.vo.SearchVO;
import com.lts.domain.vo.TweetVO;
import com.lts.enumeration.SearchType;
import com.lts.result.Result;
import com.lts.service.ITweetsService;
import com.lts.service.IUsersService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequiredArgsConstructor
public class SearchController {
    private final IUsersService usersService;
    private final ITweetsService iTweetsService;
    @GetMapping("/Search")
    @ApiOperation("搜索功能")
    public Result<SearchVO> search(
            @RequestParam String qu,
            @RequestParam(required = false) String f) {
        SearchVO searchVO = new SearchVO();
        SearchType searchType = SearchType.fromValue(f);
        if (SearchType.USER.equals(searchType)) {
            // 使用参数 qu 查询用户名
            List<Users> users = usersService.findUserByEmailOrUsername(qu);
            searchVO.setUsers(users);
        }else {
            List<TweetVO> tweetVO=iTweetsService.tweetSearch(qu);
            searchVO.setTweets(tweetVO);
        }
        return Result.success(searchVO);
    }
}
