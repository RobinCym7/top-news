package com.cym.user.controller.v1;

import com.cym.user.service.ApUserService;
import com.cym.model.common.dtos.LoginDto;
import com.cym.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class ApUserLoginController {

    @Autowired
    private ApUserService apUserService;

    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto loginDto){
        return apUserService.login(loginDto);
    }
}
