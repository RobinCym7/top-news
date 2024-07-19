package com.cym.model.wemedia.dtos;

import lombok.Data;

@Data
public class WmChannelDto {

    /**
     * 频道id，有则表示更新，无则表示保存
     */
    private Integer id;

    /**
     * 频道描述
     */
    private String description;

    /**
     * 频道名称
     */
    private String name;

    /**
     * 序号
     */
    private Integer ord;

    /**
     * 状态
     */
    private Boolean status;
}
