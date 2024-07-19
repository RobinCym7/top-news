package com.cym.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.user.mapper.AuthMapper;
import com.cym.user.service.ApUserService;
import com.cym.user.service.AuthService;
import com.cym.model.common.dtos.PageResponseResult;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.user.dtos.AuthDto;
import com.cym.model.user.pojos.ApUserRealname;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, ApUserRealname> implements AuthService {

    @Autowired
    private ApUserService apUserService;

    /**
     * 查询所有待审核用户
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult listAuth(AuthDto dto) {
        // 1.参数合法性验证
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        Integer size = dto.getSize();
        if (size == null || size == 0){
            size = 10;
        }

        size = Math.min(50, size);

        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<ApUserRealname> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus() != null){
            lambdaQueryWrapper.eq(ApUserRealname::getStatus, dto.getStatus());
        }

        lambdaQueryWrapper.orderByAsc(ApUserRealname::getSubmitedTime);
        page = page(page, lambdaQueryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 审核通过
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult pass(AuthDto dto) {
        // 参数验证
        if(dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询审核用户
        ApUserRealname user = getOne(Wrappers.<ApUserRealname>lambdaQuery().eq(ApUserRealname::getUserId, dto.getId()));
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_DATA_NOT_EXIST);
        }

        // 创建用户（添加用户信息到ApUser表中）
        apUserService.add(user);

        // 修改审核状态
        user.setStatus((short) 9);
        updateById(user);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 未通过
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult failed(AuthDto dto) {
        // 1.检查参数
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.填写驳回原因及修改状态
        ApUserRealname userRealname = getOne(Wrappers.<ApUserRealname>lambdaQuery().eq(ApUserRealname::getUserId, dto.getId()));
        if (userRealname == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_DATA_NOT_EXIST);
        }
        userRealname.setStatus((short) 2); // 修改状态为未通过
        userRealname.setReason(dto.getMsg()); // 添加驳回原因
        updateById(userRealname);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
