package com.cym.search.service;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.search.dtos.HistorySearchDto;
import com.cym.model.search.dtos.UserSearchDto;
import org.springframework.scheduling.annotation.Async;

public interface UserSearchService {

    /**
     * 插入历史记录到mongodb
     * @param keyword
     * @param userId
     */
    @Async
    public void insert(String keyword, Integer userId);

    /**
     * 搜索历史记录
     * @return
     */
    public ResponseResult load();

    /**
     * 删除搜索记录
     * @param dto
     * @return
     */
    ResponseResult deleteSearch(HistorySearchDto dto);

    /**
     * 用户搜索联想词
     * @param dto
     * @return
     */
    ResponseResult associateSearch(UserSearchDto dto);
}
