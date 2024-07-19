package com.cym.article.feign;

import com.cym.apis.article.IArticleClient;
import com.cym.article.service.ArticleService;
import com.cym.model.article.dto.ArticleDto;
import com.cym.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleClient implements IArticleClient {
    @Autowired
    private ArticleService articleService;


    @PostMapping("/api/v1/article/save")
    @Override
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return articleService.saveArticle(dto);
    }

    @PostMapping("/api/v1/article/getArticle")
    @Override
    public ResponseResult getArticle(Long articleId) {
        return articleService.getArticle(articleId);
    }
}
