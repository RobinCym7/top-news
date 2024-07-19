package com.cym.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.cym.apis.wemedia.IWemediaClient;
import com.cym.article.mapper.ArticleMapper;
import com.cym.article.service.HotArticleService;
import com.cym.common.constants.ArticleConstants;
import com.cym.common.redis.CacheService;
import com.cym.model.article.pojos.ApArticle;
import com.cym.model.article.vo.HotArticleVo;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.wemedia.pojos.WmChannel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private IWemediaClient wemediaClient;
    @Autowired
    private CacheService cacheService;

    /**
     * 热点文章计算
     */
    @Override
    public void computeHotArticle() {
        // 1.查询前5天的文章数据
        Date date = DateTime.now().minusDays(5).toDate();
        List<ApArticle> articleList = articleMapper.findArticleListBy5Days(date);

        // 2.计算文章的分值
        List<HotArticleVo> hotArticleVoList = computeArticle(articleList);


        // 3.为每个频道缓存30条分值较高的文章
        cacheTagToRedis(hotArticleVoList);


    }

    /**
     * 为每个频道缓存30条分值较高的文章
     * @param hotArticleVoList
     */
    private void cacheTagToRedis(List<HotArticleVo> hotArticleVoList) {
        ResponseResult responseResult = wemediaClient.getChannels();
        if (responseResult.getCode().equals(200)){
            String jsonString = JSON.toJSONString(responseResult.getData());
            List<WmChannel> wmChannels = JSON.parseArray(jsonString, WmChannel.class);

            // 检索处每个频道的文章
            if (wmChannels != null && wmChannels.size() >0){
                for (WmChannel wmChannel : wmChannels) {
                    List<HotArticleVo> hotArticleVos = hotArticleVoList.stream().filter(x -> x.getChannelId().equals(wmChannel.getId())).collect(Collectors.toList());
                    // 给文章进行排序，取30条分值较高的文章存入redis中，key：频道id，value
                   sortAndCache(hotArticleVos, ArticleConstants.HOT_ARTICLE_FIRST_PAGE +wmChannel.getId());
                }
            }
        }

        // 给文章进行排序，取30条分值较高的文章存入redis中，key：频道id，value
        sortAndCache(hotArticleVoList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);

    }

    private void sortAndCache(List<HotArticleVo> hotArticleVos, String key){
        hotArticleVos = hotArticleVos.stream().sorted(Comparator.comparing(HotArticleVo::getScore).reversed()).collect(Collectors.toList());
        if (hotArticleVos.size() > 30){
            hotArticleVos.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(hotArticleVos));

    }

    /**
     * 计算文章分值
     * @param articleList
     * @return
     */
    private List<HotArticleVo> computeArticle(List<ApArticle> articleList) {
        List<HotArticleVo> hotArticleVoList = new ArrayList<>();
        if (articleList != null || articleList.size() >0){
            for (ApArticle apArticle : articleList) {
                HotArticleVo hotArticleVo = new HotArticleVo();
                BeanUtils.copyProperties(apArticle, hotArticleVo);
                Integer score = coumuteScore(hotArticleVo);
                hotArticleVo.setScore(score);
                hotArticleVoList.add(hotArticleVo);
            }
        }
        return hotArticleVoList;
    }

    /**
     * 计算文章具体分值
     * @param hotArticleVo
     * @return
     */
    private Integer coumuteScore(HotArticleVo hotArticleVo) {
        Integer score = 0;
        if (hotArticleVo.getLikes() != null){
            score += hotArticleVo.getLikes() * ArticleConstants.LIKES_WEIGHT;
        }
        if (hotArticleVo.getViews() != null){
            score += hotArticleVo.getViews() * ArticleConstants.VIEW_WEIGHT;
        }
        if (hotArticleVo.getComment() != null){
            score += hotArticleVo.getComment() * ArticleConstants.COMMENT_WEIGHT;
        }
        if (hotArticleVo.getCollection() != null){
            score += hotArticleVo.getCollection() * ArticleConstants.COLLECTION_WEIGHT;
        }
        return score;
    }
}
