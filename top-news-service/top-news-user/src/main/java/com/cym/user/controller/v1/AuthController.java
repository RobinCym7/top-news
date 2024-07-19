package com.cym.user.controller.v1;

import com.cym.user.service.AuthService;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.user.dtos.AuthDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody AuthDto dto){
        return authService.listAuth(dto);
    }

    @PostMapping("/authPass")
    public ResponseResult pass(@RequestBody AuthDto dto){
        return authService.pass(dto);
    }

    @PostMapping("/authFail")
    public ResponseResult failed(@RequestBody AuthDto dto){
        return authService.failed(dto);
    }
}
