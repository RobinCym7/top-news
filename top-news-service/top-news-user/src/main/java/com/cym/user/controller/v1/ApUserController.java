package com.cym.user.controller.v1;

import com.cym.user.service.ApUserFollowService;
import com.cym.model.behavior.dto.LikesBehaviorDto;
import com.cym.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ApUserController {

    @Autowired
    private ApUserFollowService apUserFollowService;

    @PostMapping("/user_follow")
    private ResponseResult follow(@RequestBody LikesBehaviorDto dto){
        return apUserFollowService.follow(dto);
    }

}
