package com.cym.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.article.dto.ArticleBehaviorDto;
import com.cym.model.article.dto.ArticleDto;
import com.cym.model.article.dto.ArticleHomeDto;
import com.cym.model.article.pojos.ApArticle;
import com.cym.model.common.dtos.ResponseResult;

public interface ArticleService extends IService<ApArticle> {
    /**
     * 加载文章方法，
     * @param dto
     * @param loadType
     * @return
     */
    ResponseResult load(ArticleHomeDto dto, Short loadType);

    /**
     * 文章保存方法
     * @param dto
     * @return
     */
    ResponseResult saveArticle(ArticleDto dto);

    /**
     * 根据articleId获取文章
     * @param articleId
     * @return
     */
    ResponseResult getArticle(Long articleId);

    /**
     * 文章行为加载
     * @param dto
     * @return
     */
    ResponseResult articleBehavior(ArticleBehaviorDto dto);

    /**
     * 加载文章列表
     * @param dto
     * @param type  1 加载更多   2 加载最新
     * @param firstPage  true  是首页  flase 非首页
     * @return
     */
    public ResponseResult load2(ArticleHomeDto dto,Short type,boolean firstPage);
}
