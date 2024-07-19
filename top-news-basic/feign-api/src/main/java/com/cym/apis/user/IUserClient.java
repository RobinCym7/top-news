package com.cym.apis.user;

import com.cym.model.user.dtos.UserFollowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-user")
public interface IUserClient {
    @PostMapping("/api/v1/article/load_article_behavior")
    public boolean isFollow(@RequestBody UserFollowDto dto);
}
