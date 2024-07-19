package com.cym.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.behavior.dto.LikesBehaviorDto;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.user.dtos.UserFollowDto;
import com.cym.model.user.pojos.ApUserFollow;

public interface ApUserFollowService extends IService<ApUserFollow> {

    /**
     * 用户关注行为
     * @param dto
     * @return
     */
    ResponseResult follow(LikesBehaviorDto dto);

    /**
     * 是否关注
     * @return
     */
    boolean isFollow(UserFollowDto dto);
}
