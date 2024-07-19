package com.cym.wemedia.service;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.pojos.WmNews;

public interface WmNewsAutoScanService {

    /**
     * 自媒体文章审核
     * @param id 自媒体文章id
     */
    public void autoScanWmNews(Integer id);

    public ResponseResult saveAppArticle(WmNews wmNews);
}
