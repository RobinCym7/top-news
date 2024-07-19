package com.cym.apis.article.fallback;

import com.cym.apis.article.IArticleClient;
import com.cym.model.article.dto.ArticleDto;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        log.error("触发降级机制！");
        return ResponseResult.errorResult(AppHttpCodeEnum.MATERIAL_AUDIT_FAIL);
    }

    @Override
    public ResponseResult getArticle(Long articleId) {
        log.error("触发降级机制！");
        return ResponseResult.errorResult(AppHttpCodeEnum.MATERIAL_AUDIT_FAIL);
    }
}
