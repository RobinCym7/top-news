package com.cym.model.wemedia.dtos;

import com.cym.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class WmMaterialDto extends PageRequestDto {

    /**
     * 是否收藏
     * 1. 查询收藏的
     * 0. 未收藏
     */
    short isCollection;
}
