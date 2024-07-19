package com.cym.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cym.model.article.pojos.ApArticleContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleContentMapper extends BaseMapper<ApArticleContent> {
}
