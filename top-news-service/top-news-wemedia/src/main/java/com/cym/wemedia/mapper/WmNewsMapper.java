package com.cym.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cym.model.wemedia.pojos.WmNews;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WmNewsMapper extends BaseMapper<WmNews> {

    List<WmNews> listByChannelId(Integer channelId);
}
