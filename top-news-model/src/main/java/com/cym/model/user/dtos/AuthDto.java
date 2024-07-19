package com.cym.model.user.dtos;

import lombok.Data;

@Data
public class AuthDto {

    /**
     * id
     */
    private Integer id;
    /**
     * 说明消息
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
     * 状态
     */
    private Integer status;

}
