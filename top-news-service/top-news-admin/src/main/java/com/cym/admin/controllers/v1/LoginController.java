package com.cym.admin.controllers.v1;

import com.cym.admin.service.LoginService;
import com.cym.model.admin.dtos.AdminLoginDto;
import com.cym.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdminLoginDto dto){
        return loginService.login(dto);
    }
}
