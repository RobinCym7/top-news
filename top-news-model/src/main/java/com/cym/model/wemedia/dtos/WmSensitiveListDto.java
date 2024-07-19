package com.cym.model.wemedia.dtos;

import lombok.Data;

@Data
public class WmSensitiveListDto {

    /**
     * 敏感词
     */
    private String name;
    /**
     * 当前页
     */
    private Integer page;
    /**
     * 页大小
     */
    private Integer size;

}
