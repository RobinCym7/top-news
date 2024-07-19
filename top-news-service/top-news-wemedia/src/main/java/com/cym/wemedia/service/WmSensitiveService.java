package com.cym.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmSensitiveDto;
import com.cym.model.wemedia.dtos.WmSensitiveListDto;
import com.cym.model.wemedia.pojos.WmSensitive;

public interface WmSensitiveService extends IService<WmSensitive> {
    /**
     * 敏感词保存
     * @return
     */
    ResponseResult save(WmSensitiveDto dto);

    /**
     * 敏感词列表
     * @param dto
     * @return
     */
    ResponseResult listSensitive(WmSensitiveListDto dto);

    /**
     * 删除敏感词
     * @param id
     * @return
     */
    ResponseResult delete(Integer id);

    /**
     * 更新敏感词
     * @param dto
     * @return
     */
    ResponseResult updateSensitive(WmSensitiveDto dto);
}
