package com.cym.behavior.controller.v1;

import com.cym.behavior.service.BehaviorService;
import com.cym.model.behavior.dto.CollectionBehaviorDto;
import com.cym.model.behavior.dto.LikesBehaviorDto;
import com.cym.model.behavior.dto.ReadBehaviorDto;
import com.cym.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BehaviorController {

    @Autowired
    private BehaviorService behaviorService;

    @PostMapping("/likes_behavior")
    public ResponseResult likes(@RequestBody LikesBehaviorDto dto){
        return behaviorService.likes(dto);
    }

    @PostMapping("/read_behavior")
    public ResponseResult read(@RequestBody ReadBehaviorDto dto){
        return behaviorService.read(dto);
    }

    @PostMapping("/collection_behavior")
    public ResponseResult collection(@RequestBody CollectionBehaviorDto dto){
        return behaviorService.collection(dto);
    }

}
