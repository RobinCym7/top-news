package com.cym.model.user.dtos;

import lombok.Data;

@Data
public class UserRelationDto {

    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 作者ID
     */
    private Integer authorId;
    /**
     * 操作
     */
    private Short operation;
}
