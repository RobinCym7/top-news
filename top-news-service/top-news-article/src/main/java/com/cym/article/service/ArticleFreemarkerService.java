package com.cym.article.service;

import com.cym.model.article.pojos.ApArticle;
import org.springframework.stereotype.Service;

@Service
public interface ArticleFreemarkerService {

    /**
     * 生成静态html文件上传到MinIO中
     * @param article
     * @param content
     */
    void buildArticleToMinIO(ApArticle article, String content);

}
