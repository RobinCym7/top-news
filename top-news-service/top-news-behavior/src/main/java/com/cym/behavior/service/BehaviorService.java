package com.cym.behavior.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.article.pojos.ApArticle;
import com.cym.model.behavior.dto.CollectionBehaviorDto;
import com.cym.model.behavior.dto.LikesBehaviorDto;
import com.cym.model.behavior.dto.ReadBehaviorDto;
import com.cym.model.common.dtos.ResponseResult;

public interface BehaviorService extends IService<ApArticle> {

    /**
     * 点赞行为
     *
     * @param dto
     * @return
     */
    ResponseResult likes(LikesBehaviorDto dto);

    /**
     * 阅读行为
     * @param dto
     * @return
     */
    ResponseResult read(ReadBehaviorDto dto);

    /**
     * 用户收藏
     * @param dto
     * @return
     */
    ResponseResult collection(CollectionBehaviorDto dto);
}
