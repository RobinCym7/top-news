package com.cym.article;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cym.article.mapper.ArticleContentMapper;
import com.cym.article.service.ArticleService;
import com.heima.file.service.FileStorageService;
import com.cym.model.article.pojos.ApArticle;
import com.cym.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;


@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private Configuration configuration;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ArticleService articleService;

    @Test
    public void convertHtml() throws IOException, TemplateException {
        // 1.从数据库中获取文章内容
        ApArticleContent articleContent = articleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, "1383828014629179393L"));
        if (articleContent != null && StringUtils.isNotBlank(articleContent.getContent())){
            // 2.将文章内容利用freemarker技术转化为html
            Template template = configuration.getTemplate("article.ftl");
            HashMap<String, Object> map = new HashMap<>();
            map.put("content", JSONArray.parseArray(articleContent.getContent()));
            StringWriter out = new StringWriter();
            template.process(map, out);

            // 3.把html上传到minio
            ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", articleContent.getArticleId() + ".html", inputStream);

            // 4.将路径写回数据库中
            articleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId, articleContent.getArticleId())
                    .set(ApArticle::getStaticUrl, path));
        }
    }
}
