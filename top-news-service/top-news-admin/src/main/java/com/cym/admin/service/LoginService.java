package com.cym.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.admin.dtos.AdminLoginDto;
import com.cym.model.admin.pojo.AdUser;
import com.cym.model.common.dtos.ResponseResult;

public interface LoginService extends IService<AdUser> {

    /**
     * admin端登录服务
     * @param dto
     * @return
     */
    public ResponseResult login(AdminLoginDto dto);

}
