package com.cym.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.wemedia.mapper.WmMaterialMapper;
import com.heima.file.service.FileStorageService;
import com.cym.model.common.dtos.PageResponseResult;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.wemedia.dtos.WmMaterialDto;
import com.cym.model.wemedia.pojos.WmMaterial;
import com.cym.utils.thread.WmThreadLocalUtil;
import com.cym.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 图片上传接口
     *
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        // 1.参数校验
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.图片上传Minio
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String path = null;
        try {
            path = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("图片上传成功！");
        } catch (IOException e) {
            log.error("图片上传失败！");
            throw new RuntimeException(e);
        }

        // 3.将地址写入数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        wmMaterial.setUrl(path);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setType((short) 0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        // 返回结果
        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult materialList(WmMaterialDto wmMaterialDto) {
        // 1.参数验证
        wmMaterialDto.checkParam();
        short isCollection = wmMaterialDto.getIsCollection();
        if (!(isCollection == 0 || isCollection == 1)) {
            // isCollection参数即不是1也不是0，则设置未默认值0
            isCollection = (short) 0;
        }

        // 2.分页查询
        Page page = new Page(wmMaterialDto.getPage(), wmMaterialDto.getSize());
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 2.1是否收藏
        if(isCollection == 1){
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection, wmMaterialDto.getIsCollection());
        }
        // 2.2按照用户查询
        lambdaQueryWrapper.eq(WmMaterial::getUserId, WmThreadLocalUtil.getUser().getId());
        // 2.3按照时间倒序
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        page = page(page, lambdaQueryWrapper);
        //2.4 结果返回
        ResponseResult responseResult = new PageResponseResult(wmMaterialDto.getPage(), wmMaterialDto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;

    }
}
