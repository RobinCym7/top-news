package com.cym.article.service.impl;

import com.cym.article.ArticleApplication;
import com.cym.article.service.HotArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class HotArticleServiceImplTest {
    @Autowired
    private HotArticleService hotArticleService;

    @Test
    public void computeArticleTest(){
        hotArticleService.computeHotArticle();
    }
}