package com.cym.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cym.model.user.pojos.ApUserRealname;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper extends BaseMapper<ApUserRealname> {
}
