package com.cym.search.controller.v1;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.search.dtos.UserSearchDto;
import com.cym.search.service.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/associate")
public class AssociateSearchController {

    @Autowired
    private UserSearchService userSearchService;

    @PostMapping("/search")
    public ResponseResult search(@RequestBody UserSearchDto userSearchDto){
        return userSearchService.associateSearch(userSearchDto);
    }

}
