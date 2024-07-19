package com.cym.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.model.common.dtos.PageResponseResult;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.wemedia.dtos.WmSensitiveDto;
import com.cym.model.wemedia.dtos.WmSensitiveListDto;
import com.cym.model.wemedia.pojos.WmSensitive;
import com.cym.wemedia.service.WmSensitiveService;
import com.cym.wemedia.mapper.WmSensitiveMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WmSensitiveSeriviceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {

    /**
     * 敏感词保存
     * @return
     */
    @Override
    public ResponseResult save(WmSensitiveDto dto) {
        // 1.参数验证
        if (dto == null || StringUtils.isBlank(dto.getSensitives())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.查询是否已经存在敏感词
        WmSensitive sensitive = getOne(Wrappers.<WmSensitive>lambdaQuery().eq(WmSensitive::getSensitives, dto.getSensitives()));
        if (sensitive != null){
            // 2.1敏感词已经存在
            return ResponseResult.errorResult(AppHttpCodeEnum.SENSITIVE_EXIST);
        }

        // 保存敏感词
        sensitive = new WmSensitive();
        sensitive.setSensitives(dto.getSensitives());
        sensitive.setCreatedTime(new Date());
        save(sensitive);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 敏感词列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult listSensitive(WmSensitiveListDto dto) {
        // 1.验证参数合法性
        if (dto.getSize() == 0){
            dto.setSize(10);
        }
        Integer size = Math.max(50, dto.getSize());

        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmSensitive> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(dto.getName())){
            lambdaQueryWrapper.eq(WmSensitive::getSensitives, dto.getName());
        }
        lambdaQueryWrapper.orderByAsc(WmSensitive::getCreatedTime);
        page = page(page, lambdaQueryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;
    }

    /**
     * 删除敏感词
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult delete(Integer id) {
        // 1.验证参数合法性
        if (id==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.删除敏感词
        boolean flag = removeById(id);
        if (!flag){
            // 2.1删除失败
            return ResponseResult.okResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 更新敏感词
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult updateSensitive(WmSensitiveDto dto) {
        // 1.参数合法性验证
        if(dto == null || StringUtils.isBlank(dto.getSensitives())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.更新参数
        WmSensitive sensitive = getOne(Wrappers.<WmSensitive>lambdaQuery().eq(WmSensitive::getId, dto.getId()));
        if (sensitive == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 更新敏感词
        sensitive.setSensitives(dto.getSensitives());
        updateById(sensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
