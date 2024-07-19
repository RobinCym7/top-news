package com.cym.model.wemedia.dtos;

import lombok.Data;

@Data
public class WmNewsListDto {

    /**
     * 文章id
     */
    private Integer id;
    /**
     * 文章信息
     */
    private String msg;
    /**
     * 当前页
     */
    private Integer page;
    /**
     * 页大小
     */
    private Integer size;
    /**
     * 文章状态
     */
    private Integer status;
    /**
     * 文章标题
     */
    private String title;
}
