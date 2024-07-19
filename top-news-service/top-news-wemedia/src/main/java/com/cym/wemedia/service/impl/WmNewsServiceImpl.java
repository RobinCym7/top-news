package com.cym.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.common.constants.WemediaConstants;
import com.cym.common.constants.WmNewsMessageConstants;
import com.cym.common.exception.CustomException;
import com.cym.model.common.dtos.PageResponseResult;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.wemedia.dtos.WmNewsDto;
import com.cym.model.wemedia.dtos.WmNewsAuthDto;
import com.cym.model.wemedia.dtos.WmNewsListDto;
import com.cym.model.wemedia.dtos.WmNewsPageReqDto;
import com.cym.model.wemedia.pojos.WmMaterial;
import com.cym.model.wemedia.pojos.WmNews;
import com.cym.model.wemedia.pojos.WmNewsMaterial;
import com.cym.model.wemedia.pojos.WmUser;
import com.cym.wemedia.mapper.WmMaterialMapper;
import com.cym.wemedia.mapper.WmNewsMapper;
import com.cym.wemedia.service.WmNewsAutoScanService;
import com.cym.wemedia.service.WmNewsService;
import com.cym.wemedia.service.WmUserService;
import com.cym.utils.thread.WmThreadLocalUtil;
import com.cym.wemedia.mapper.WmNewsMaterialMapper;
import com.cym.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;
    @Autowired
    private WmNewsTaskService wmNewsTaskService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private WmUserService wmUserService;

    /**
     * 按条件获取文章
     * @param dto
     * @return
     */
    @Override
    public ResponseResult getList(WmNewsPageReqDto dto) {
        // 1.参数验证
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        // 获取当前登录人信息
        WmUser user = WmThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2.查询文章
        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 2.1 用户
        lambdaQueryWrapper.eq(WmNews::getUserId, WmThreadLocalUtil.getUser().getId());
        // 2.2 状态
        if(dto.getStatus() != null){
            lambdaQueryWrapper.eq(WmNews::getStatus,dto.getStatus());
        }
        // 2.3 所属频道ID
        if(dto.getChannelId() != null){
            lambdaQueryWrapper.eq(WmNews::getChannelId,dto.getChannelId());
        }
        // 2.4时间范围查询
        if(dto.getBeginPubDate()!=null && dto.getEndPubDate()!=null){
            lambdaQueryWrapper.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());
        }
        // 2.5关键字模糊查询
        if(StringUtils.isNotBlank(dto.getKeyword())){
            lambdaQueryWrapper.like(WmNews::getTitle,dto.getKeyword());
        }

        // 2.4发布时间倒序查询
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);
        page = page(page, lambdaQueryWrapper);

        // 3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 保存或修改文章
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submit(WmNewsDto dto) {
        // 1.参数验证
        if (dto == null || dto.getContent() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.保存或修改文章
        WmNews wmNews = new WmNews();
        // 属性拷贝，将dto中的属性拷贝到wmnews中
        BeanUtils.copyProperties(dto, wmNews);
        // 封面图片 dto中的为list，wmnews中的为string，因此无法拷贝过去，需要手动设置
        if(dto.getImages() != null && dto.getImages().size() >0){
            // [abc.jpd, dsa.jpg] --> abc.jpd, dsa.jpg
            String imageStr = StringUtils.join(dto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        //如果当前封面类型为自动 -1
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }
        saveOrUpdateNews(wmNews);

        // 3.判断是否为草稿，如果为草稿则结束当前方法
        if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 4.不是草稿，保存文章内容中的图片与素材的关系
        // 获取文章内容中的图片信息 --> [da.jpg, daa.jpg]
        List<String> materials = extractUrlInfo(dto.getContent());
        saveRelativeInfoForContent(materials, wmNews.getId());
        
        // 5.不是草稿，保存文章封面图片与素材的关系
        saveRelativeInfoForCover(dto, wmNews, materials);

        // 6.审核文章
        // wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
        wmNewsTaskService.addNewsToTask(wmNews.getId(), wmNews.getPublishTime());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 文章上下架
     * @param dto
     * @return
     */
    @Override
    public ResponseResult downOrUp(WmNewsDto dto) {
        // 1.检查参数
        if (dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }

        // 3.判断文章是否已发表
        if (!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章未发布，无法上下架");
        }

        // 4.修改文章enable
        if (dto.getEnable() != null && dto.getEnable() > -1 && dto.getEnable() < 2){
            update(Wrappers.<WmNews>lambdaUpdate().set(WmNews::getEnable, dto.getEnable()).eq(WmNews::getId, wmNews.getId()));
            if (wmNews.getArticleId() != null){
                HashMap<String, Object> map = new HashMap<>();
                map.put("articleId", wmNews.getArticleId());
                map.put("enable", dto.getEnable());
                kafkaTemplate.send(WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC, JSON.toJSONString(map));
            }
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 人工审核文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult listVo(WmNewsListDto dto) {
        // System.out.println(dto);
        // 1.参数检验
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        Integer size = dto.getSize();
        if (size == null){
            size = 10;
        }
        size = Math.min(50, size);

        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (dto.getStatus() !=null){
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        if (dto.getTitle() !=null){
            lambdaQueryWrapper.like(WmNews::getTitle, dto.getTitle());
        }
        lambdaQueryWrapper.orderByDesc(WmNews::getSubmitedTime);
        page = page(page, lambdaQueryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 查询某篇具体文章
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult getOneNews(Integer id) {
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews news = getOne(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getId, id));
        if (news == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.ARTICLE_NOT_EXIST);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.convertValue(news, Map.class);
        WmUser wmUser = wmUserService.getUserById(news.getUserId());
        if (wmUser == null){
            map.put("authorName", "");
        }else {
            map.put("authorName", wmUser.getName());
        }
        return ResponseResult.okResult(map);
    }

    /**
     * 文章审核未通过
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult fail(WmNewsAuthDto dto) {
        // 1.参数验证
        if (dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2.查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.ARTICLE_NOT_EXIST);
        }

        // 3.修改文章状态，填写驳回原因
        wmNews.setStatus((short) 2); // 2代表审核失败
        wmNews.setReason(dto.getMsg());
        updateById(wmNews);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 文章审核通过
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult pass(WmNewsAuthDto dto) {
        // 1.参数验证
        if(dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews wmNews = getById(dto.getId());
        if (wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.ARTICLE_NOT_EXIST);
        }

        // 发布文章
        wmNews.setStatus((short) 4); // 设置为审核成功
        wmNews.setReason("人工审核通过");
        wmNewsAutoScanService.saveAppArticle(wmNews);
        updateById(wmNews);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 第一个功能，如果当封面图片类型为自动，则设置封面类型的数据
     * 匹配规则：
     * 1.如果内容图片大于等于1，小于3，则单图，type设为1
     * 2.如果内容图片大于等于3，则多图，type设为3
     * 3.如果内容没有图片，则无图，type设为0
     * 第二个功能，保存封面图片与素材的关系
     * @param dto
     * @param wmNews
     * @param materials
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {
        List<String> images = dto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            // 多图
            if (materials.size() >= 3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            }else if (materials.size()>=1 && materials.size() <3){
                // 单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            }else {
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            // 修改文章
            if (images != null && images.size() > 0){
                wmNews.setImages(StringUtils.join(images, ","));
            }
            updateById(wmNews);
        }
        if (images !=null && images.size() > 0){
            saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }
    }

    /**
     * 处理文章内容图片与素材的关系
     * @param materials
     * @param id
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer id) {
        saveRelativeInfo(materials, id, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存文章图片与素材关系到数据库中
     * @param materials
     * @param id
     * @param type
     */
    private void saveRelativeInfo(List<String> materials, Integer id, Short type) {
        if (materials != null && !materials.isEmpty()){
            // 通过图片url查询素材的id
            List<WmMaterial> materialList = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));
            // 判断素材是否有效
            if(materialList == null || materialList.size() ==0){
                // 没有素材，需要手动抛出异常，第一个功能是提示调用者素材失效了，第二个功能是进行数据的回滚
                throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
            }
            if (materialList.size() != materials.size()){
                // 查询出的素材数量和传来的素材数量不等
                throw new CustomException(AppHttpCodeEnum.MATERIAL_REFERENCE_FAIL);
            }

            // 查询出的素材数量和传来的素材数量相等，获取素材的id
            List<Integer> idList = materialList.stream().map(WmMaterial::getId).collect(Collectors.toList());
            // 批量保存
            wmNewsMaterialMapper.saveRelations(idList, id, type);
        }
    }

    /**
     * 提取文章内容中的图片信息
     * @param content
     * @return
     */
    private List<String> extractUrlInfo(String content) {
        ArrayList<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps){
            if (map.get("type").equals("image")){
                String imageUrl = (String)map.get("value");
                materials.add(imageUrl);
            }
        }
        return materials;
    }

    /**
     * 保存或修改文章
     * @param wmNews
     */
    private void saveOrUpdateNews(WmNews wmNews) {
        // 补全属性
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short)1); // 默认上架

        if (wmNews.getId() == null){
            // 保存文章
            save(wmNews);
        }else {
            // 修改文章
            // 删除文章图片与素材关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            updateById(wmNews);
        }
    }
}
