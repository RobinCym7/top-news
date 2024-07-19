package com.cym.wemedia.client;

import com.cym.apis.wemedia.IWemediaClient;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WemediaClient implements IWemediaClient {

    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/api/v1/channel/list")
    @Override
    public ResponseResult getChannels() {
        return wmChannelService.getAll();
    }
}