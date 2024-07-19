package com.cym.wemedia.controller.v1;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmMaterialDto;
import com.cym.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    /**
     * 图片上传
     * @param multipartFile
     * @return ResponseResult
     */
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile){
        return wmMaterialService.uploadPicture(multipartFile);
    }

    /**
     * 素材查询
     * @param wmMaterialDto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult materialList(@RequestBody WmMaterialDto wmMaterialDto){
        return wmMaterialService.materialList(wmMaterialDto);
    }
}
