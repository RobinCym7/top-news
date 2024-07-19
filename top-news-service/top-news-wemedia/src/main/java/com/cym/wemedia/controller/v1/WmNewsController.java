package com.cym.wemedia.controller.v1;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.dtos.WmNewsDto;
import com.cym.model.wemedia.dtos.WmNewsAuthDto;
import com.cym.model.wemedia.dtos.WmNewsListDto;
import com.cym.model.wemedia.dtos.WmNewsPageReqDto;
import com.cym.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 按条件获取所有文章
     * @return
     */
    @PostMapping("/list")
    public ResponseResult getList(@RequestBody WmNewsPageReqDto dto){
        return wmNewsService.getList(dto);
    }

    /**
     * 保存或修改新闻
     * @param dto
     * @return
     */
    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.submit(dto);
    }

    /**
     * 文章上下架
     * @param dto
     * @return
     */
    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
        return wmNewsService.downOrUp(dto);
    }

    /**
     * 文章审核列表
     * @param dto
     * @return
     */
    @PostMapping("/list_vo")
    public ResponseResult listVo(@RequestBody WmNewsListDto dto){
        return wmNewsService.listVo(dto);
    }

    /**
     * 查询某篇具体文章
     * @param id
     * @return
     */
    @GetMapping("/one_vo/{id}")
    public ResponseResult getOneNews(@PathVariable Integer id){
        return wmNewsService.getOneNews(id);
    }

    /**
     * 文章审核未通过
     * @param dto
     * @return
     */
    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody WmNewsAuthDto dto){
        return wmNewsService.fail(dto);
    }

    /**
     * 文章审核通过
     * @param dto
     * @return
     */
    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody WmNewsAuthDto dto){
        return wmNewsService.pass(dto);
    }
}
