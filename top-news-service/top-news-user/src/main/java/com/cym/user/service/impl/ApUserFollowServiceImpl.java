package com.cym.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.apis.article.IArticleClient;
import com.cym.user.mapper.ApUserFollowMapper;
import com.cym.user.service.ApUserFanService;
import com.cym.user.service.ApUserFollowService;
import com.cym.user.service.ApUserService;
import com.cym.model.article.pojos.ApArticle;
import com.cym.model.behavior.dto.LikesBehaviorDto;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.user.dtos.UserFollowDto;
import com.cym.model.user.pojos.ApUser;
import com.cym.model.user.pojos.ApUserFollow;
import com.cym.utils.thread.ApUserThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class ApUserFollowServiceImpl extends ServiceImpl<ApUserFollowMapper, ApUserFollow> implements ApUserFollowService {

    @Autowired
    private IArticleClient articleClient;
    @Autowired
    private ApUserFanService apUserFanService;
    @Autowired
    private ApUserService apUserService;


    /**
     * 用户关注行为
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult follow(LikesBehaviorDto dto) {
        // 1.获取当前用户
        Integer id = ApUserThreadLocalUtil.getUser().getId();
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 2远程调用article微服务，获取作者id
        ResponseResult responseResult = articleClient.getArticle(dto.getArticleId());
        if (responseResult.getCode() != AppHttpCodeEnum.SUCCESS.getCode()){
            // 2.1远程调用失败
            return ResponseResult.errorResult(AppHttpCodeEnum.AUTHOR_ID_GET_FAIL);
        }

        // 2.2获取作者id
        String data = (String) responseResult.getData();
        ApArticle apArticle = JSON.parseObject(data, ApArticle.class);
        Long authorId = apArticle.getAuthorId();
        String authorName = apArticle.getAuthorName();

        if (dto.getOperation().equals((short) 0)){
            // 关注
            log.info("关注作者");
            ApUserFollow apUserFollow = new ApUserFollow();
            apUserFollow.setUserId(id);
            apUserFollow.setFollowId(authorId);
            apUserFollow.setFollowName(authorName);
            apUserFollow.setLevel(1);
            apUserFollow.setIsNotice(false);
            apUserFollow.setCreateTime(new Date());
            save(apUserFollow);

            ApUser apUser = apUserService.getUserById(id);
            apUserFanService.addFans(apUser, authorId, authorName);

        }else {
            // 取消关注
            log.info("取消关注");
            apUserFanService.removeFans(id, authorId);
            remove(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getUserId, id)
                    .eq(ApUserFollow::getFollowId, authorId));
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 是否关注
     *
     * @return
     */
    @Override
    public boolean isFollow(UserFollowDto dto) {
        ApUserFollow follow = getOne(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getUserId, dto.getUserId()).eq(ApUserFollow::getFollowId, dto.getAuthorId()));
        boolean flag = false;
        if (follow != null){
            flag = true;
        }
        return flag;
    }
}
