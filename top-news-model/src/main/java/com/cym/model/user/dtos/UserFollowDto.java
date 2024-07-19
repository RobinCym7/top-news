package com.cym.model.user.dtos;

import lombok.Data;

@Data
public class UserFollowDto {
    private Integer userId;
    private Integer authorId;
}
