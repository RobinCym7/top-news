package com.cym.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmMaterialDto;
import com.cym.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传接口
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 查询素材
     * @param wmMaterialDto
     * @return
     */
    ResponseResult materialList(WmMaterialDto wmMaterialDto);
}
