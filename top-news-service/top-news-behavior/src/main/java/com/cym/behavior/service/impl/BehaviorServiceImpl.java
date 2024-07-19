package com.cym.behavior.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.behavior.mapper.ApArticleMapper;
import com.cym.behavior.service.BehaviorService;
import com.cym.common.redis.CacheService;
import com.cym.model.article.pojos.ApArticle;
import com.cym.model.behavior.dto.CollectionBehaviorDto;
import com.cym.model.behavior.dto.LikesBehaviorDto;
import com.cym.model.behavior.dto.ReadBehaviorDto;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.utils.thread.ApUserThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BehaviorServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements BehaviorService {

    @Autowired
    private CacheService cacheService;

    /**
     * 点赞行为
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult likes(LikesBehaviorDto dto) {
        // 1.参数验证
        if(dto == null || dto.getArticleId() == null || dto.getOperation() == null || dto.getType() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle article = getOne(Wrappers.<ApArticle>lambdaQuery().eq(ApArticle::getId, dto.getArticleId()));
        if (article == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.ARTICLE_NOT_EXIST);
        }

        // 2.根据type施行点赞行为:0文章，1动态，2评论，
        if (dto.getType().equals((short) 0)){
            // 文章情况
            Integer likes = article.getLikes();
            if (likes ==null){
                likes = 0;
            }
            if (dto.getOperation().equals((short) 0)){
                // 2.1.点赞

                article.setLikes(likes +1); // 文章点赞数量加1
                updateById(article);
                // 保存到redis中
                String key = "User.Likes." +  ApUserThreadLocalUtil.getUser().getId();
                cacheService.lLeftPush(key, String.valueOf(dto.getArticleId()));
            }else {
                // 2.2.取消点赞
                article.setLikes(likes -1); // 文章点赞数量减1
                updateById(article);
                // 保存到redis中
                String key = "User.Likes." +  ApUserThreadLocalUtil.getUser().getId();
                cacheService.lRemove(key, 0, String.valueOf(dto.getArticleId()));
            }
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 阅读行为
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult read(ReadBehaviorDto dto) {
        // 1.参数检查
        if (dto == null || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.查询文章
        ApArticle article = getOne(Wrappers.<ApArticle>lambdaQuery().eq(ApArticle::getId, dto.getArticleId()));
        if (article == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.ARTICLE_NOT_EXIST);
        }

        // 3.存入redis中
        String key = "article.views."+dto.getArticleId();
        String pop = cacheService.lLeftPop(key);
        if (pop ==null){
            pop = "0";
        }
        int views = Integer.parseInt(pop) + dto.getCount();
        cacheService.lLeftPush(key, String.valueOf(views));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 用户收藏
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult collection(CollectionBehaviorDto dto) {
        // 1.参数检查
        if (dto == null || dto.getEntryId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.判断用户操作0：收藏，1：取消收藏
        if (dto.getOperation().equals((short) 0)){
            // 2.1收藏
            String key = "user.collection." + ApUserThreadLocalUtil.getUser().getId();
            cacheService.lLeftPush(key, dto.getEntryId().toString());
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
