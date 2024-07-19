package com.cym.user.clients;


import com.cym.apis.user.IUserClient;
import com.cym.user.service.ApUserFollowService;
import com.cym.model.user.dtos.UserFollowDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserClient implements IUserClient {

    @Autowired
    private ApUserFollowService apUserFollowService;

    @PostMapping("/api/v1/article/load_article_behavior")
    @Override
    public boolean isFollow(@RequestBody UserFollowDto dto) {
        return apUserFollowService.isFollow(dto);
    }
}
