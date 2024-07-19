package com.cym.model.wemedia.dtos;

import lombok.Data;

@Data
public class WmChannelListDto {

    /**
     * 频道名称，非必须
     */
    private String name;

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer size;

    /**
     * 是否启用
     */
    private Boolean status;
}
