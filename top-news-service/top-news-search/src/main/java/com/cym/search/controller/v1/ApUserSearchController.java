package com.cym.search.controller.v1;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.search.dtos.HistorySearchDto;
import com.cym.search.service.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
public class ApUserSearchController {

    @Autowired
    private UserSearchService userSearchService;

    @PostMapping("/load")
    public ResponseResult load(){
        return userSearchService.load();
    }

    @PostMapping("del")
    public ResponseResult deleteSearch(@RequestBody HistorySearchDto dto){
        return userSearchService.deleteSearch(dto);
    }
}
