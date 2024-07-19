package com.cym.article.job;

import com.cym.article.service.HotArticleService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ComputeScoreJob {

    @Autowired
    private HotArticleService hotArticleService;

    @XxlJob("articleScoreCompute")
    public void computeScore(){
        log.info("文章分值计算开始。。。。。");
        hotArticleService.computeHotArticle();
        log.info("文章分值计算结束。。。。。");
    }

}
