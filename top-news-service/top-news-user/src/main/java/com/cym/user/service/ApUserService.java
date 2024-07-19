package com.cym.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.common.dtos.LoginDto;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.user.pojos.ApUser;
import com.cym.model.user.pojos.ApUserRealname;


public interface ApUserService extends IService<ApUser> {


    /**
     * 用户登录
     * @param loginDto
     * @return
     */
    ResponseResult login(LoginDto loginDto);

    /**
     * 创建用户
     * @param user
     * @return
     */
    void add(ApUserRealname user);

    /**
     * 根据用户id查询用户
     * @param id
     * @return
     */
    ApUser getUserById(Integer id);
}
