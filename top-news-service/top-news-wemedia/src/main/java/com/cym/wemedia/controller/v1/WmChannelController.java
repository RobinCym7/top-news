package com.cym.wemedia.controller.v1;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmChannelDto;
import com.cym.model.wemedia.dtos.WmChannelListDto;
import com.cym.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;

@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {

    @Autowired
    private WmChannelService channelService;

    /**
     * 查询所有频道
     * @return
     */
    @GetMapping("/channels")
    public ResponseResult getAllChannels(){
        return channelService.getAll();
    }

    /**
     * 保存频道
     * @param dto
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody WmChannelDto dto) throws InvocationTargetException, IllegalAccessException {
        return channelService.saveChannel(dto);
    }

    @PostMapping("/list")
    public ResponseResult list(@RequestBody WmChannelListDto dto){
        return channelService.listChannels(dto);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmChannelDto dto){
        return channelService.updateChannel(dto);
    }

    @GetMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Integer id){
        return channelService.deleteChannel(id);
    }


}
