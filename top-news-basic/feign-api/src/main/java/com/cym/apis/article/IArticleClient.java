package com.cym.apis.article;

import com.cym.apis.article.fallback.IArticleClientFallback;
import com.cym.model.article.dto.ArticleDto;
import com.cym.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-article", fallback = IArticleClientFallback.class)
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto);

    @PostMapping("/api/v1/article/getArticle")
    public ResponseResult getArticle(@RequestBody Long articleId);
}
