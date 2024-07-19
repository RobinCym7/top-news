package com.cym.article.controller.v1;

import com.cym.article.service.ArticleService;
import com.cym.common.constants.ArticleConstants;
import com.cym.model.article.dto.ArticleBehaviorDto;
import com.cym.model.article.dto.ArticleHomeDto;
import com.cym.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 加载首页
     * @param dto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto){
        return articleService.load2(dto, ArticleConstants.LOADTYPE_LOAD_MORE, true);
    }

    /**
     * 加载更多
     * @param dto
     * @return
     */
    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto){
        return articleService.load(dto, ArticleConstants.LOADTYPE_LOAD_MORE);
    }

    /**
     * 加载最新
     * @param dto
     * @return
     */
    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto){
        return articleService.load(dto, ArticleConstants.LOADTYPE_LOAD_NEW);
    }

    @PostMapping("/load_article_behavior")
    public ResponseResult loadArticleBehavior(@RequestBody ArticleBehaviorDto dto){
        return articleService.articleBehavior(dto);
    }



}
