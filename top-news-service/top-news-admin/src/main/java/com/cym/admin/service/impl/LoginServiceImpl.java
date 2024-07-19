package com.cym.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.admin.mapper.AdUserMapper;
import com.cym.admin.service.LoginService;
import com.cym.model.admin.dtos.AdminLoginDto;
import com.cym.model.admin.pojo.AdUser;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

@Service
public class LoginServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements LoginService {

    /**
     * admin端登录服务
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(AdminLoginDto dto) {
        // 1.参数验证
        if (dto == null || StringUtils.isBlank(dto.getName()) || StringUtils.isBlank(dto.getPassword())){
            // 1.1参数非法
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.查询用户
        AdUser adUser = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, dto.getName()));
        if (adUser == null){
            // 2.1如果用户不存在
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_DATA_NOT_EXIST);
        }

        // 3.密码比对
        String password = dto.getPassword();
        String salt = adUser.getSalt();
        String pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if (!pwd.equals(adUser.getPassword())){
            // 3.1密码错误
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 4.密码正确，返回数据
        String token = AppJwtUtil.getToken(adUser.getId().longValue());
        HashMap<String, Object> map = new HashMap<>();
        adUser.setPassword("");
        adUser.setSalt("");
        map.put("user", adUser);
        map.put("token", token);
        return ResponseResult.okResult(map);
    }

}
