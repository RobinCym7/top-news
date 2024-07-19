package com.cym.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmLoginDto;
import com.cym.model.wemedia.pojos.WmUser;

public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    public ResponseResult login(WmLoginDto dto);

    /**
     * 查询用户信息
     * @param id
     * @return
     */
    public WmUser getUserById(Integer id);

}