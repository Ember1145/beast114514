package com.lts.controller.user;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lts.context.userContext;
import com.lts.domain.dto.FollowDTO;
import com.lts.domain.dto.UserHomeDTO;
import com.lts.domain.dto.userDTO;
import com.lts.domain.po.Follows;
import com.lts.domain.po.UserStatistics;
import com.lts.domain.po.Users;
import com.lts.domain.vo.*;
import com.lts.enumeration.RelationshipStatus;
import com.lts.enumeration.SearchType;
import com.lts.result.Result;
import com.lts.service.IFollowsService;
import com.lts.service.ITweetsService;
import com.lts.service.IUserStatisticsService;
import com.lts.service.IUsersService;
import com.lts.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
@MapperScan("com.lts.mapper")
@Api(tags = "接口")
@Slf4j
public class UsersController {
    private final IUsersService iUsersService;
    private final ITweetsService iTweetsService;
    private final IFollowsService iFollowsService;
    private final IUserStatisticsService iUserStatisticsService;
    private final AliOssUtil aliOssUtil;
    @PostMapping("/register")
    @ApiOperation("注册功能")
    public Result saveOrUpdate(@RequestBody userDTO userDTO) {
         iUsersService.userRegister(userDTO);
        return Result.success();
    }
    @PostMapping("/login")
    @ApiOperation("登录")
    public Result<tokenVO> login(@RequestBody userDTO userDTO) {
        tokenVO tokenVo= iUsersService.login(userDTO);
        return Result.success(tokenVo);
    }
    @PostMapping("/update")
    @ApiOperation("修改用户信息")
    public Result updateUserInfo(@RequestBody UserHomeDTO userHomeDTO){
        Long userId = userContext.getUserId();
        String oldAvatarUrl = userHomeDTO.getOldAvatarUrl();
        String oldBackUrl = userHomeDTO.getOldBackUrl();
        if(oldAvatarUrl != null && !oldAvatarUrl.isEmpty()){
            aliOssUtil.deleteFile(oldAvatarUrl);
        }
        if(oldBackUrl != null && !oldBackUrl.isEmpty()){
            aliOssUtil.deleteFile(oldBackUrl);
        }
        Users users = BeanUtil.copyProperties(userHomeDTO, Users.class);
        users.setUserId(userId);
        iUsersService.saveOrUpdate(users);
        return Result.success();
    }
        @GetMapping("/{emailCut}")
        public Result<userPageVO> userPage(@PathVariable String emailCut){
            Long userId = userContext.getUserId();
            userPageVO userPageVO = iUsersService.queryUser(emailCut,userId);
        return Result.success(userPageVO);
    }
        @PostMapping("/follow")
        public Result<RelationVO> follow(@RequestBody FollowDTO followDTO) {
            Long myId = userContext.getUserId();
            LambdaQueryWrapper<Follows> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Follows::getFollowerId, myId)
                    .eq(Follows::getFolloweeId, followDTO.getUserId());
            Follows one = iFollowsService.getOne(queryWrapper);
            LambdaQueryWrapper<Follows> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Follows::getFollowerId, followDTO.getUserId())
                    .eq(Follows::getFolloweeId, myId);
            Follows two = iFollowsService.getOne(queryWrapper1);
            if (one == null && two == null) {
                Follows follows = Follows.builder()
                        .followerId(myId)
                        .followeeId(followDTO.getUserId())
                        .status(RelationshipStatus.FOLLOWING)
                        .build();
                boolean save = iFollowsService.save(follows);
                Follows follows1 = Follows.builder()
                        .followerId(followDTO.getUserId())
                        .followeeId(myId)
                        .status(RelationshipStatus.NOT_FOLLOWING)
                        .build();
                boolean save1 = iFollowsService.save(follows1);
                if (save&&save1) {
                    followAddCount(myId,followDTO.getUserId());
                }
                RelationVO build = RelationVO.builder().status(RelationshipStatus.FOLLOWING).build();
                return Result.success(build);
                //大家都互相关注的状态
            } else if (one!=null&&one.getStatus().equals(RelationshipStatus.MUTUAL)) {
                boolean c = updateToNotFollow(myId,followDTO.getUserId());
                boolean b = updateToFollow(followDTO.getUserId(), myId);
                if (c && b) {
                    followReduceCount(myId, followDTO.getUserId());
                }
                RelationVO build = RelationVO.builder().status(RelationshipStatus.NOT_FOLLOWING).build();
                return Result.success(build);
            }//对方关注我,我没关注对方
                else if (two != null && two.getStatus().equals(RelationshipStatus.FOLLOWING)) {
                boolean b = updateToMutual(myId, followDTO.getUserId());
                boolean b1 = updateToMutual(followDTO.getUserId(), myId);
                if (b&&b1) {
                        followAddCount(myId,followDTO.getUserId());
                    }
                RelationVO build = RelationVO.builder().status(RelationshipStatus.MUTUAL).build();
                      return Result.success(build);
                }
                //我关注对方,对方未关注我
                else if(one!=null&&one.getStatus().equals(RelationshipStatus.FOLLOWING)&&two!=null&&two.getStatus().equals(RelationshipStatus.NOT_FOLLOWING)) {
                boolean b = updateToNotFollow(myId, followDTO.getUserId());
                if(b){
                   followReduceCount(myId,followDTO.getUserId());
                }
                RelationVO build = RelationVO.builder().status(RelationshipStatus.NOT_FOLLOWING).build();
                return Result.success(build);
             }
                //已经插入数据,两边都未关注
                else if(one!=null&&one.getStatus().equals(RelationshipStatus.NOT_FOLLOWING)&&two!=null&&two.getStatus().equals(RelationshipStatus.NOT_FOLLOWING)){
                boolean b = updateToFollow(myId, followDTO.getUserId());
                if(b){
                    followAddCount(myId,followDTO.getUserId());
                }
                RelationVO build = RelationVO.builder().status(RelationshipStatus.FOLLOWING).build();
                return Result.success(build);
            }
                else{ return Result.error("关注错误");}

        }
        public void followAddCount(Long myId,Long userId){
            LambdaUpdateWrapper<UserStatistics> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserStatistics::getUserId, myId)
                    .setSql("following_count=following_count+1");
            iUserStatisticsService.update(updateWrapper);
            LambdaUpdateWrapper<UserStatistics> updateWrapper1 = new LambdaUpdateWrapper<>();
            updateWrapper1.eq(UserStatistics::getUserId, userId)
                    .setSql("followers_count=followers_count+1");
            iUserStatisticsService.update(updateWrapper1);
        }
        public void followReduceCount(Long myId,Long userId){
        LambdaUpdateWrapper<UserStatistics> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserStatistics::getUserId, myId)
                .setSql("following_count=following_count-1");
        iUserStatisticsService.update(updateWrapper);
        LambdaUpdateWrapper<UserStatistics> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.eq(UserStatistics::getUserId, userId)
                .setSql("followers_count=followers_count-1");
        iUserStatisticsService.update(updateWrapper1);
    }
    public boolean updateToNotFollow(Long myId,Long userId){
        LambdaUpdateWrapper<Follows> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Follows::getFolloweeId, userId)
                .eq(Follows::getFollowerId, myId)
                .set(Follows::getStatus, RelationshipStatus.NOT_FOLLOWING);
        return iFollowsService.update(updateWrapper);
    }
    public boolean updateToMutual(Long myId,Long userId){
        LambdaUpdateWrapper<Follows> updateWrapper3 = new LambdaUpdateWrapper<>();
        updateWrapper3.eq(Follows::getFollowerId, myId)
                .eq(Follows::getFolloweeId, userId)
                .set(Follows::getStatus, RelationshipStatus.MUTUAL);
        return iFollowsService.update(updateWrapper3);
    }
    public boolean updateToFollow(Long myId,Long userId){
        LambdaUpdateWrapper<Follows> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Follows::getFolloweeId, userId)
                .eq(Follows::getFollowerId, myId)
                .set(Follows::getStatus, RelationshipStatus.FOLLOWING);
        return iFollowsService.update(updateWrapper);
    }
    @GetMapping("/{emailCut}/reply")
    public Result<List<List<TweetVO>>> getReplyTweet(@PathVariable String emailCut,@RequestParam Integer current){
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getEmailCut,emailCut)
                .select(Users::getUserId);
        Map<String, Object> map = iUsersService.getMap(queryWrapper);
        IPage<TweetVO> reply = iTweetsService.getTopFold((Long) map.get("user_id"),current,10);
        List<TweetVO> records = reply.getRecords();
        List<List<TweetVO>> chain=new ArrayList<>();
        for (TweetVO tweetVO: records){
            List<TweetVO> topChain= new ArrayList<>();
            TweetVO top= iTweetsService.getTweetVOById(tweetVO.getParentId());
            topChain.add(tweetVO);
            topChain.add(top);
            if(top.getRealParent()!=null){
                TweetVO realTop = iTweetsService.getTweetVOByRealParent(top.getRealParent());
                topChain.add(realTop);
            }
            Collections.reverse(topChain);
            chain.add(topChain);
        }
        return Result.success(chain);
    }
}
