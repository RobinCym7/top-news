package com.cym.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baidu.aip.contentcensor.EImgType;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cym.apis.article.IArticleClient;
import com.cym.audit.service.impl.BaiDuYunAuditService;
import com.cym.model.article.dto.ArticleDto;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.pojos.WmChannel;
import com.cym.model.wemedia.pojos.WmNews;
import com.cym.model.wemedia.pojos.WmSensitive;
import com.cym.model.wemedia.pojos.WmUser;
import com.cym.wemedia.service.WmNewsAutoScanService;
import com.cym.utils.common.SensitiveWordUtil;
import com.cym.wemedia.mapper.WmChannelMapper;
import com.cym.wemedia.mapper.WmNewsMapper;
import com.cym.wemedia.mapper.WmSensitiveMapper;
import com.cym.wemedia.mapper.WmUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private BaiDuYunAuditService baiDuYunAuditService;
    @Autowired
    private WmChannelMapper wmChannelMapper;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private IArticleClient articleClient;
    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    /**
     * 文章审核
     *
     * @param id 自媒体文章id
     */
    @Override
    @Async
    // @GlobalTransactional
    public void autoScanWmNews(Integer id) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 1. 查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }

        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            // 2.审核状态为待审核的文章
            // 2.1 从内容中提取文本和图片
            Map<String, Object> textAndImages = getTextAndImage(wmNews);

            // 违禁词审核
            boolean isSensitiveSafe = handleSensitiveScan((String) textAndImages.get("content"), wmNews);
            if (!isSensitiveSafe) return; // 如果文本中有敏感词信息，直接结束

            // 3.审核内容
            boolean isTextSafe = handleTextScan((String) textAndImages.get("content"), wmNews);
            if (!isTextSafe) return; // 如果文本中有违规信息，直接结束

            // 4.审核图片
            boolean isImageSafe = handleImageScan((List<String>) textAndImages.get("images"), wmNews);
            if (!isImageSafe) return; // 如果图片中有违规信息，直接结束

            // 审核通过，保存app端的相关文章数据
            ResponseResult responseResult = saveAppArticle(wmNews);
            if(!responseResult.getCode().equals(200)){
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }

            //回填article_id
            wmNews.setArticleId((Long) responseResult.getData());
            updateWmNews(wmNews,WmNews.Status.PUBLISHED.getCode(),"审核成功");

        }
    }

    /**
     * 敏感词检查
     * @param content
     * @param wmNews
     * @return
     */
    private boolean handleSensitiveScan(String content, WmNews wmNews) {
        boolean flag = true;
        //查询所有敏感词
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        // 初始化敏感词
        SensitiveWordUtil.initMap(sensitiveList);
        // 查看文章和标题中是否包含敏感词
        Map<String, Integer> sensitiveMap = SensitiveWordUtil.matchWords(content);
        if (sensitiveMap.size() > 0){
            updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "当前文章中存在违规内容："+sensitiveMap);
            flag = false;
        }
        return flag;
    }

    /**
     * 保存app端的相关文章数据
     * @param wmNews
     * @return
     */
    public ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, articleDto);

        // 文章的布局
        articleDto.setLayout(wmNews.getType());
        // 频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null){
            articleDto.setChannelName(wmChannel.getName());
        }
        //作者
        articleDto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null){
            articleDto.setAuthorName(wmUser.getName());
        }
        //设置文章id
        if (wmNews.getArticleId() != null) {
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setCreatedTime(new Date());

        return articleClient.saveArticle(articleDto);
    }

    /**
     * 审查图片内容是否安全
     * @param images
     * @param wmNews
     * @return
     */
    private boolean handleImageScan(List<String> images, WmNews wmNews) {
        boolean flag = true;
        if (images == null || images.size() == 0 ){
            return flag;
        }
        try {
            for (String image : images) {
                JSONObject jsonObject = baiDuYunAuditService.audioImage(image, EImgType.URL);
                if (jsonObject.get("conclusion").equals("不合规")){
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "当前图片中存在违规内容");
                    break;
                }

                if (jsonObject.get("conclusion").equals("疑似") || jsonObject.get("conclusion").equals("审核失败")) {
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "当前图片中存在不确定内容");
                    break;
                }
            }
        }catch (Exception e){
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 更新文章相关信息
     * @param wmNews
     * @param type
     * @param reason
     */
    private void updateWmNews(WmNews wmNews, short type, String reason){
        wmNews.setStatus(type);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }
    /**
     * 审查文本内容是否安全
     * @param content
     * @return
     */
    private boolean handleTextScan(String content, WmNews wmNews) {
        boolean flag = true;

        if ((wmNews.getTitle() + content).length() == 0) {
            return flag;
        }
        JSONObject jsonObject = baiDuYunAuditService.auditText(wmNews.getTitle() + content);
        try {
            if (jsonObject != null) {
                // 审核未通过
                if (jsonObject.get("conclusion").equals("不合规")) {
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), "当前文章中存在违规内容");
                }

                // 不确定，需要人工审核
                if (jsonObject.get("conclusion").equals("疑似") || jsonObject.get("conclusion").equals("审核失败")) {
                    flag = false;
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), "当前文章中存在不确定内容");
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 提取文本和图片，并封装成map返回
     *
     * @param wmNews
     * @return
     */
    private Map<String, Object> getTextAndImage(WmNews wmNews) {
        // 存储存文本内容
        StringBuilder stringBuilder = new StringBuilder();
        // 存储图片内容
        List<String> images = new ArrayList<>();

        // 1.从自媒体文章的内容提取文本和图片
        if (StringUtils.isNotBlank(wmNews.getContent())) {
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }
                if (map.get("type").equals("images")) {
                    images.add((String) map.get("value"));
                }
            }
        }

        // 2.提取文章的封面图
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", stringBuilder.toString());
        resultMap.put("images", images);
        return resultMap;
    }
}
