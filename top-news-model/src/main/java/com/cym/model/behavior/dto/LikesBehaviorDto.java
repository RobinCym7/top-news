package com.cym.model.behavior.dto;

import lombok.Data;

@Data
public class LikesBehaviorDto {

    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 0点赞，1取消点赞
     */
    private Short operation;
    /**
     * 0文章  1动态   2评论
     */
    private Short type;
}
