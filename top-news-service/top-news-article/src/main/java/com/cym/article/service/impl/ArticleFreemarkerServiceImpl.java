package com.cym.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cym.article.service.ArticleFreemarkerService;
import com.cym.article.service.ArticleService;
import com.cym.common.constants.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.cym.model.article.pojos.ApArticle;
import com.cym.model.search.vos.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;

@Service
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private Configuration configuration;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 生成静态html文件上传到MinIO中
     *
     * @param article
     * @param content
     */
    @Override
    @Async
    // @GlobalTransactional
    public void buildArticleToMinIO(ApArticle article, String content) {
        // 1.从数据库中获取文章内容
        if (StringUtils.isNotBlank(content)) {
            // 2.将文章内容利用freemarker技术转化为html
            Template template = null;
            StringWriter out = new StringWriter();
            try {
                template = configuration.getTemplate("article.ftl");
                HashMap<String, Object> map = new HashMap<>();
                map.put("content", JSONArray.parseArray(content));
                template.process(map, out);
            } catch (Exception e) {
                e.printStackTrace();
            }


            // 3.把html上传到minio
            ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", article.getId() + ".html", inputStream);

            // 4.将路径写回数据库中
            articleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId, article.getId())
                    .set(ApArticle::getStaticUrl, path));

            // 5.同步到es中
            createEsArticle(article, path, content);
        }
    }

    /**
     * 同步文章到es中
     * @param article
     * @param path
     * @param content
     */
    private void createEsArticle(ApArticle article, String path, String content) {
        SearchArticleVo searchArticleVo = new SearchArticleVo();
        BeanUtils.copyProperties(article, searchArticleVo);
        searchArticleVo.setContent(content);
        searchArticleVo.setStaticUrl(path);

        kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVo));
    }
}
