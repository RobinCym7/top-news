package com.cym.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.user.pojos.ApUser;
import com.cym.model.user.pojos.ApUserFan;

public interface ApUserFanService extends IService<ApUserFan> {
    /**
     * 添加粉丝
     * @param apUser
     * @param authorId
     * @param authorName
     */
    void addFans(ApUser apUser, Long authorId, String authorName);

    /**
     * 移除粉丝
     * @param userId
     * @param authorId
     */
    void removeFans(Integer userId, Long authorId);

}
