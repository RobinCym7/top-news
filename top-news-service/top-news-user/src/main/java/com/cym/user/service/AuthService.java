package com.cym.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.user.dtos.AuthDto;
import com.cym.model.user.pojos.ApUserRealname;

public interface AuthService extends IService<ApUserRealname> {
    /**
     * 查询所有待审核用户
     * @param dto
     * @return
     */
    ResponseResult listAuth(AuthDto dto);

    /**
     * 审核通过
     * @param dto
     * @return
     */
    ResponseResult pass(AuthDto dto);

    /**
     * 未通过
     * @param dto
     * @return
     */
    ResponseResult failed(AuthDto dto);
}
