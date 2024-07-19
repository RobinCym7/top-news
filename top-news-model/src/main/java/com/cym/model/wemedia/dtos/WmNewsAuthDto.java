package com.cym.model.wemedia.dtos;

import lombok.Data;

@Data
public class WmNewsAuthDto {
    /**
     * 文章id
     */
    private Integer id;
    /**
     * 文章未通过理由
     */
    private String msg;
}
