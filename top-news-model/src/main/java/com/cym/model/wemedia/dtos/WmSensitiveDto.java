package com.cym.model.wemedia.dtos;

import lombok.Data;

@Data
public class WmSensitiveDto {

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * id
     */
    private Integer id;

    /**
     * 敏感词
     */
    private String sensitives;


}
