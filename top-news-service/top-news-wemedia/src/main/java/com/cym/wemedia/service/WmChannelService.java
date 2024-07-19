package com.cym.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmChannelDto;
import com.cym.model.wemedia.dtos.WmChannelListDto;
import com.cym.model.wemedia.pojos.WmChannel;

import java.lang.reflect.InvocationTargetException;

public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询所有频道
     * @return
     */
    ResponseResult getAll();

    /**
     * 保存频道
     * @param dto
     * @return
     */
    ResponseResult saveChannel(WmChannelDto dto) throws InvocationTargetException, IllegalAccessException;

    /**
     * 查询频道
     * @param dto
     * @return
     */
    ResponseResult listChannels(WmChannelListDto dto);

    /**
     * 更新频道
     * @param dto
     * @return
     */
    ResponseResult updateChannel(WmChannelDto dto);

    /**
     * 频道删除
     * @param id
     * @return
     */
    ResponseResult deleteChannel(Integer id);
}
