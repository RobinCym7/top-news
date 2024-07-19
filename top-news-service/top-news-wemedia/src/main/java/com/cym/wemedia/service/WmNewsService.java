package com.cym.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmNewsDto;
import com.cym.model.wemedia.dtos.WmNewsAuthDto;
import com.cym.model.wemedia.dtos.WmNewsListDto;
import com.cym.model.wemedia.dtos.WmNewsPageReqDto;
import com.cym.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 按条件获取文章
     * @param dto
     * @return
     */
    ResponseResult getList(WmNewsPageReqDto dto);

    /**
     * 保存或修改文章
     * @param dto
     * @return
     */
    ResponseResult submit(WmNewsDto dto);

    /**
     * 文章是否上下架
     * @param dto
     * @return
     */
    ResponseResult downOrUp(WmNewsDto dto);

    /**
     * 人工审核文章列表
     *
     * @param dto
     * @return
     */
    ResponseResult listVo(WmNewsListDto dto);

    /**
     * 查询某篇具体文章
     * @param id
     * @return
     */
    ResponseResult getOneNews(Integer id);

    /**
     * 文章审核未通过
     * @param dto
     * @return
     */
    ResponseResult fail(WmNewsAuthDto dto);

    /**
     * 文章审核通过
     * @param dto
     * @return
     */
    ResponseResult pass(WmNewsAuthDto dto);

}
