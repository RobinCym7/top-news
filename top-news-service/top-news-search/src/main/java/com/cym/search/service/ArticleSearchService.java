package com.cym.search.service;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.search.dtos.UserSearchDto;

import java.io.IOException;

public interface ArticleSearchService {

    /**
     * 用户搜索
     * @param dto
     * @return
     */
    public ResponseResult search(UserSearchDto dto) throws IOException;

}
