package com.cym.model.article.dto;

import com.cym.model.article.pojos.ApArticle;
import lombok.Data;

@Data
public class ArticleDto extends ApArticle {
    /**
     * 文章内容
     */
    private String content;
}
