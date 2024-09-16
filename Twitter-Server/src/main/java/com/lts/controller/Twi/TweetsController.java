package com.lts.controller.Twi;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lts.context.userContext;
import com.lts.domain.dto.TwiDTO;
import com.lts.domain.dto.rabbitmq.TweetsAndTagsDTO;
import com.lts.domain.po.*;
import com.lts.domain.vo.*;
import com.lts.result.Result;
import com.lts.service.*;
import com.lts.utils.AliOssUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@RestController
@RequiredArgsConstructor
public class TweetsController {

    private final AliOssUtil aliOssUtil;
    private final ITweetsService iTweetsService;
    private final IUsersService iUsersService;
    private final ITweetInteractionsService iTweetInteractionsService;
    private final IUserStatisticsService iUserStatisticsService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ITweetTagsService iTweetTagsService;
    private final IUserTagsService iUserTagsService;
    private final MessageService messageService;
    @PostMapping("/save")
    public Result<TweetVO> submitForm(@RequestBody TwiDTO twiDTO) {
        Long userId = userContext.getUserId();
        Tweets tweets = Tweets.builder()
                .userId(userId)
                .media(twiDTO.getMedia())
                .content(twiDTO.getContent())
                .parentId(twiDTO.getParentId())
                .realParent(twiDTO.getRealParent())
                .createdAt(LocalDateTime.now()).build();
        boolean saved = iTweetsService.save(tweets);
        if (!saved) {
            return Result.error("发推失败");
        }
        TweetsAndTagsDTO tweetsAndTagsDTO = TweetsAndTagsDTO.of(tweets.getTweetId(),tweets.getParentId(),twiDTO.getTag(),userId);
        messageService.sendTweetCreation(tweetsAndTagsDTO);
        TweetVO tweetVO = new TweetVO();
        tweetVO.setTweetId(tweets.getTweetId());
        tweetVO.setCreatedAt(tweets.getCreatedAt());
        return Result.success(tweetVO);
    }
    @GetMapping("/{emailCut}/status/{tweetId}")
    public Result<TweetDetailVO> getTweetDetail(@PathVariable String emailCut, @PathVariable Long tweetId,
                                                @RequestParam Integer current) {
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getEmailCut,emailCut)
                .select(Users::getUserId);
        Map<String, Object> map = iUsersService.getMap(queryWrapper);
        List<TweetVO> top = iTweetsService.getTopChain(tweetId);
        List<List<TweetVO>> topChain=new ArrayList<>();
        topChain.add(top);
        IPage<TweetVO> commentPage = iTweetsService.findCommentsByTweetId(tweetId, current, 10);//所有顶层评论
        List<List<TweetVO>> focusChains = new ArrayList<>();
        List<TweetVO> normalComments = new ArrayList<>();
        for (TweetVO comment : commentPage.getRecords()) {
            boolean hasFocusReply = iTweetsService.hasFocusReply((Long) map.get("user_id"), comment.getTweetId());
            if (hasFocusReply) {
                // 如果包含推主的回复，则构建完整的回复链
                List<TweetVO> chain = new ArrayList<>();
                chain.add(comment);
                TweetVO earliestReply = getEarliestReply(comment.getTweetId());
                while (earliestReply != null) {
                    chain.add(earliestReply);
                    earliestReply = getEarliestReply(earliestReply.getTweetId());
                }
                // 将包含推主回复的完整回复链添加到二维数组中
                focusChains.add(chain);
            } else {
                // 否则，将顶层评论加入普通评论列表
                normalComments.add(comment);
            }
        }
        // 构建返回结果（包含普通顶层评论和含推主回复的评论链）
        TweetDetailVO tweetDetailVO = TweetDetailVO.builder()
                .focusChains(focusChains)
                .commentVOList(normalComments)
                .topChain(topChain)
                .build();
        return Result.success(tweetDetailVO);
    }
    private TweetVO getEarliestReply(Long tweetId) {
        // 这假设已经有一个按时间顺序获取最早评论的方法。
        return iTweetsService.findEarliestReplyByParentId(tweetId);
    }

@GetMapping("/queryStatus")
    public Result<TweetStatusVO> queryStatus(@RequestParam Long tweetId){
    Long userId = userContext.getUserId();
    TweetStatusVO tweetStatusVO = iTweetsService.queryStatus(tweetId, userId);
    return Result.success(tweetStatusVO);
}
    public boolean deleteCommentCounts(Long tweetId,int size) {
        while (tweetId != null) {
            // 根据 tweetId 获取推文/评论
            LambdaQueryWrapper<Tweets> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Tweets::getTweetId,tweetId)
                    .select(Tweets::getParentId);
            Map<String, Object> map = iTweetsService.getMap(queryWrapper);
            // 更新当前推文/评论的评论计数
            LambdaUpdateWrapper<TweetInteractions> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TweetInteractions::getTweetId, tweetId)
                    .setSql("comment_count = comment_count-" + size);
            iTweetInteractionsService.update(updateWrapper);
            // 向上移动到父级推文/评论
            if (map != null && map.containsKey("parent_id")) {
                tweetId = (Long)map.get("parent_id");
            } else {
                // 如果 map 为 null 或者不存在 parent_id，中断循环
                break;
            }
        }
        return true;
    }
    @DeleteMapping("/delete/{tweetId}")
    @Transactional
    public Result deleteTweet(@PathVariable Long tweetId) {
        LambdaQueryWrapper<Tweets> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tweets::getTweetId, tweetId)
                .select(Tweets::getTweetId, Tweets::getMedia);
        List<Map<String, Object>> maps = iTweetsService.listMaps(queryWrapper);
        List<String> allMediaUrls = new ArrayList<>();
        Set<Long> allTweetIds = new HashSet<>();
        int allTweetSize = findAllTweetIdsAndMediaUrls(maps, allTweetIds, allMediaUrls);
        deleteCommentCounts(tweetId,allTweetSize);
        if(!allMediaUrls.isEmpty()){
            aliOssUtil.deleteFiles(allMediaUrls);
        }
        iTweetsService.removeBatchByIds(new ArrayList<>(allTweetIds));
        return Result.success();
    }
    public int findAllTweetIdsAndMediaUrls(
            List<Map<String, Object>> maps,
            Set<Long> allTweetIds,
            List<String> allMediaUrls
    ) {
        for (Map<String, Object> map : maps) {
            Long tweetId = (Long) map.get("tweet_id");
            Object mediaObject = map.get("media");
            String mediaJson = (String) mediaObject;
            List<String> mediaList = JSON.parseArray(mediaJson, String.class);
            if (mediaList != null) {
                allMediaUrls.addAll(mediaList);
            }
            if (tweetId != null) {
                allTweetIds.add(tweetId);
                List<Map<String, Object>> childMaps = iTweetsService.listMaps(new QueryWrapper<Tweets>().eq("parent_id", tweetId));
                if (!childMaps.isEmpty()) {
                    findAllTweetIdsAndMediaUrls(childMaps, allTweetIds, allMediaUrls);
                }
            }
        }
        return allTweetIds.size();
    }
    @GetMapping("/deliver")
    public Result<List<TweetVO>> getDeliver(){
        Long userId = userContext.getUserId();
        LambdaQueryWrapper<UserTags> eq = new LambdaQueryWrapper<UserTags>().eq(UserTags::getUserId, userId);
        UserTags one = iUserTagsService.getOne(eq);
        Set<String> toBePushed= new HashSet<>();
        Set<String> histories = stringRedisTemplate.opsForSet().members("tweet:histories:" + userId);
        //如果自己有标签画像则根据标签随机获取推送
        if(one!=null) {
            for (String tag : one.getTag()) {
                if (tag.equals("picture")) {
                    List<String> strings = stringRedisTemplate.opsForZSet().randomMembers("pictureDeliver:", 3);
                    if (strings != null) {
                        toBePushed.addAll(strings);
                    }
                } else if (tag.equals("music")) {
                    List<String> strings = stringRedisTemplate.opsForZSet().randomMembers("musicDeliver:", 3);
                    if (strings != null) {
                        toBePushed.addAll(strings);
                    }
                }
            }
        }else {
            //获取冷处理推送
            List<String> string1 = stringRedisTemplate.opsForZSet().randomMembers("baseDeliver:", 2);
            if (string1 != null) {
                toBePushed.addAll(string1);
            }
        }
        //历史取交集去重
        if (histories != null) {
        Set<String> intersection = new HashSet<>(toBePushed);
            intersection.retainAll(histories);
            toBePushed.removeAll(intersection);
        }
        //个人收到的订阅
        Set<String> range = stringRedisTemplate.opsForZSet().range("individual:" + userId, 0, 2);
        if(range!=null){
            toBePushed.addAll(range);
            stringRedisTemplate.opsForZSet().removeRange("individual:" + userId,0,2);
        }
        if(toBePushed.isEmpty()){
            return Result.success();
        }else {
            //一部分推送,一部分加入历史防止重推
            for(String tweetId : toBePushed) {
                stringRedisTemplate.opsForSet().add("tweet:histories:" + userId, tweetId);
            }
            LambdaQueryWrapper<Tweets> in = new LambdaQueryWrapper<Tweets>().in(Tweets::getTweetId, toBePushed);
            List<TweetVO> deliverTweet = iTweetsService.getDeliverTweet(in);
            return Result.success(deliverTweet);
        }
    }
    @DeleteMapping("/deliver/refresh")
    public Result deleteTweet() {
        Long userId = userContext.getUserId();
        stringRedisTemplate.delete("tweet:histories:" + userId);
        return Result.success();
    }
}
