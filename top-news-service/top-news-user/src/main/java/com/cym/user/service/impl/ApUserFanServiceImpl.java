package com.cym.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.user.mapper.ApUserFanMapper;
import com.cym.user.service.ApUserFanService;
import com.cym.model.user.pojos.ApUser;
import com.cym.model.user.pojos.ApUserFan;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ApUserFanServiceImpl extends ServiceImpl<ApUserFanMapper, ApUserFan> implements ApUserFanService {
    /**
     * 添加粉丝
     *
     * @param apUser
     * @param authorId
     * @param authorName
     */
    @Override
    public void addFans(ApUser apUser, Long authorId, String authorName) {
        ApUserFan apUserFan = new ApUserFan();
        apUserFan.setUserId(authorId);
        apUserFan.setFansId(apUser.getId());
        apUserFan.setFansName(apUser.getName());
        apUserFan.setLevel(1);
        apUserFan.setIsDisplay(false);
        apUserFan.setIsShieldLetter(false);
        apUserFan.setIsShieldComment(false);
        apUserFan.setCreatedTime(new Date());

        save(apUserFan);
    }

    /**
     * 移除粉丝
     * @param userId
     * @param authorId
     */
    @Override
    public void removeFans(Integer userId, Long authorId) {
        remove(Wrappers.<ApUserFan>lambdaQuery().eq(ApUserFan::getUserId, userId).eq(ApUserFan::getFansId, authorId));
    }

}
