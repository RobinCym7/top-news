package com.cym.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cym.model.article.dto.ArticleHomeDto;
import com.cym.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<ApArticle> {
    public List<ApArticle> loadArticle(ArticleHomeDto dto, Short loadType);

    public List<ApArticle> findArticleListBy5Days(@Param("date") Date date);
}
