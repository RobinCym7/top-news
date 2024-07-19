package com.cym.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.model.common.dtos.PageResponseResult;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.wemedia.dtos.WmChannelDto;
import com.cym.model.wemedia.dtos.WmChannelListDto;
import com.cym.model.wemedia.pojos.WmChannel;
import com.cym.model.wemedia.pojos.WmNews;
import com.cym.wemedia.service.WmChannelService;
import com.cym.wemedia.mapper.WmChannelMapper;
import com.cym.wemedia.mapper.WmNewsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    /**
     * 查询所有频道
     * @return
     */
    @Override
    public ResponseResult getAll() {
        return ResponseResult.okResult(list());
    }

    /**
     * 保存频道
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveChannel(WmChannelDto dto) throws InvocationTargetException, IllegalAccessException {
        // 1.参数验证
        if (dto == null || StringUtils.isBlank(dto.getName())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.验证频道名是否重复
        WmChannel wmChannel = getOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getName, dto.getName()));
        if (wmChannel != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_EXIST);
        }

        // 3.保存频道
        wmChannel = new WmChannel();
        wmChannel.setName(dto.getName());
        wmChannel.setOrd(dto.getOrd());
        wmChannel.setStatus(dto.getStatus());
        wmChannel.setDescription(dto.getDescription());

        wmChannel.setIsDefault(false);
        wmChannel.setCreatedTime(new Date());
        save(wmChannel);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 查询频道
     * @param dto
     * @return
     */
    @Override
    public ResponseResult listChannels(WmChannelListDto dto) {
        // 1.检验参数
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        Integer size = dto.getSize();
        if (size == null || size==0){
            size = 10;
        }

        // 确保文章不超过五十
        size = Math.min(50, size);

        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmChannel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(dto.getName())){
            lambdaQueryWrapper.eq(WmChannel::getName, dto.getName());
        }

        if (dto.getStatus() != null){
            lambdaQueryWrapper.eq(WmChannel::getStatus, dto.getSize());
        }
        lambdaQueryWrapper.orderByAsc(WmChannel::getCreatedTime);
        page = page(page, lambdaQueryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 更新频道
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult updateChannel(WmChannelDto dto) {
        // 1.参数检查
        if (dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmChannel channel = getOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getId, dto.getId()));
        if (channel == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.检查频道是否被设为禁用
        if (!dto.getStatus()){
            // 2.1被设置为禁用，检查是否有其他文章引用该频道
            List<WmNews> wmNewsList = wmNewsMapper.listByChannelId(dto.getId());

            if (wmNewsList.size() > 0){
                // 频道被引用
                return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_USED);
            }
        }

        // 频道未被引用
        BeanUtils.copyProperties(dto, channel);
        updateById(channel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 频道删除
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteChannel(Integer id) {
        // 检查参数
        if(id ==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.检查频道是否被引用
        List<WmNews> wmNewsList = wmNewsMapper.listByChannelId(id);
        if (wmNewsList.size() > 0){
            // 频道被引用，禁止删除
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_USED);
        }

        // 频道未被引用，可以删除
        boolean b = removeById(id);
        if (!b){
            // 删除失败
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_DELETE_FAILED);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
