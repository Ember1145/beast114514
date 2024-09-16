package com.lts.controller.Twi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lts.context.userContext;
import com.lts.domain.dto.ShareDTO;
import com.lts.domain.po.Shares;
import com.lts.domain.po.TweetInteractions;
import com.lts.domain.po.Users;
import com.lts.domain.vo.TweetVO;
import com.lts.result.Result;
import com.lts.service.ISharesService;
import com.lts.service.ITweetInteractionsService;
import com.lts.service.ITweetsService;
import com.lts.service.IUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ShareController {
    private  final ISharesService iSharesService;
    private  final ITweetInteractionsService iTweetInteractionsService;
    private final IUsersService usersService;
    private final ITweetsService iTweetsService;
    @PostMapping("/updateShare")
    public Result updateShare(@RequestBody ShareDTO shareDTO) {
        Long userId = userContext.getUserId();
        boolean success = updateOrSaveShare(userId, shareDTO.getTweetId(), shareDTO.getShared());
        if (success) {
            return Result.success("更新状态成功");
        } else {
            return Result.error("更新状态失败");
        }
    }
    public boolean updateOrSaveShare(Long userId, Long tweetId, Integer shared) {
        if(shared.equals(0)){
            LambdaUpdateWrapper<Shares> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            LambdaUpdateWrapper<Shares> set = lambdaUpdateWrapper
                    .eq(Shares::getUserId, userId)
                    .eq(Shares::getTweetId, tweetId)
                    .set(Shares::getShared,shared);
            iSharesService.update(set);
            LambdaUpdateWrapper<TweetInteractions> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper1.eq(TweetInteractions::getTweetId,tweetId)
                    .setSql("share_count = share_count - 1");
            iTweetInteractionsService.update(lambdaUpdateWrapper1);
            return iSharesService.update(set);
        }
        else {
            QueryWrapper<Shares> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("tweet_id", tweetId);
            // 尝试查找记录
            Shares existingShare = iSharesService.getOne(queryWrapper);
            LambdaUpdateWrapper<TweetInteractions> lambdaUpdateWrapper2 = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper2.eq(TweetInteractions::getTweetId,tweetId)
                    .setSql("share_count = share_count + 1");
            iTweetInteractionsService.update(lambdaUpdateWrapper2);
            // 如果存在记录，则更新点赞状态
            if (existingShare != null) {
                existingShare.setShared(shared);
                return iSharesService.updateById(existingShare);
            } else {
                // 如果不存在记录，则添加新的点赞记录
                Shares shares = Shares.builder().shared(shared)
                        .tweetId(tweetId)
                        .userId(userId).build();
                return iSharesService.save(shares);
            }
        }
    }
    @GetMapping("/{emailCut}/shares")
    public Result<List<TweetVO>> getShareAndMy(@PathVariable String emailCut, @RequestParam Integer current){
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getEmailCut,emailCut)
                .select(Users::getUserId);
        Map<String, Object> map = usersService.getMap(queryWrapper);
        IPage<TweetVO> shareAndMy= iTweetsService.getShareAndMy((Long)map.get("user_id"),current,10);
        List<TweetVO> records = shareAndMy.getRecords();
        return Result.success(records);
    }
}
