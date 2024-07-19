package com.cym.wemedia.controller.v1;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmSensitiveDto;
import com.cym.model.wemedia.dtos.WmSensitiveListDto;
import com.cym.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {

    @Autowired
    private WmSensitiveService wmSensitiveService;

    /**
     * 保存敏感词
     * @param dto
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody WmSensitiveDto dto){
        return wmSensitiveService.save(dto);
    }

    /**
     * 查询敏感词
     * @param dto
     * @return
     */
    @PostMapping("list")
    public ResponseResult list(@RequestBody WmSensitiveListDto dto){
        return wmSensitiveService.listSensitive(dto);
    }

    /**
     * 删除敏感词
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Integer id){
        return wmSensitiveService.delete(id);
    }

    /**
     * 更新敏感词
     * @param dto
     * @return
     */
    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmSensitiveDto dto){
        return wmSensitiveService.updateSensitive(dto);
    }

}
