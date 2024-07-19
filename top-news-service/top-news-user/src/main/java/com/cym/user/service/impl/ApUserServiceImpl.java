package com.cym.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.apis.article.IArticleClient;
import com.cym.user.mapper.ApUserMapper;
import com.cym.user.service.ApUserService;
import com.cym.model.common.dtos.LoginDto;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.user.pojos.ApUser;
import com.cym.model.user.pojos.ApUserRealname;
import com.cym.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;

@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    @Autowired
    private ApUserMapper apUserMapper;
    @Autowired
    private IArticleClient articleClient;

    @Override
    public ResponseResult login(LoginDto loginDto) {
        // 1. 正常登录
        if (StringUtils.isNotBlank(loginDto.getPassword()) && StringUtils.isNotBlank(loginDto.getPhone())){
            // 1.1 去数据库中查询用户数据
            ApUser dbUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, loginDto.getPhone()));
            if (dbUser == null){
                return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
            }

            // 1.2 用户存在：密码比对
            String password = loginDto.getPassword();
            String salt = dbUser.getSalt();
            String pswd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!pswd.equals(dbUser.getPassword())){
                // 1.2.1 密码错误
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            // 1.2.2 密码正确，准备返回数据
            String token = AppJwtUtil.getToken(dbUser.getId().longValue());
            HashMap<String, Object> map = new HashMap<>();
            map.put("token", token);
            dbUser.setPassword("");
            dbUser.setSalt("");
            map.put("user", dbUser);
            return ResponseResult.okResult(map);
        }
        // 2. 游客登录
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", AppJwtUtil.getToken(0L));
        return ResponseResult.okResult(map);
    }

    /**
     * 创建用户
     *
     * @param user
     * @return
     */
    @Override
    public void add(ApUserRealname user) {

        // 创建用户
        ApUser apUser = new ApUser();
        apUser.setName(user.getName());
        String defaultPwd = "123"; // 默认密码
        String defaultSalt = "abc"; // 默认盐
        String pwd = DigestUtils.md5DigestAsHex((defaultPwd + defaultSalt).getBytes());
        apUser.setPassword(pwd);
        apUser.setSalt(defaultSalt);
        apUser.setPhone("19107083600"); // 默认手机号
        apUser.setSex(true); // 默认性别
        apUser.setStatus(true); //默认正常状态
        apUser.setFlag((short) 1); // 自媒体人
        apUser.setCreatedTime(new Date());
        save(apUser);
    }

    /**
     * 根据用户id查询用户
     *
     * @param id
     * @return
     */
    @Override
    public ApUser getUserById(Integer id) {
        return getById(id);
    }

}
