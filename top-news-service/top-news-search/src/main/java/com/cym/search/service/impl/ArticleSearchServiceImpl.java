package com.cym.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.search.dtos.UserSearchDto;
import com.cym.model.user.pojos.ApUser;
import com.cym.search.service.ArticleSearchService;
import com.cym.search.service.UserSearchService;
import com.cym.utils.thread.ApUserThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private UserSearchService userSearchService;

    /**
     * 用户搜索
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult search(UserSearchDto dto) throws IOException {
        // 1.检查参数
        if (dto == null || StringUtils.isBlank(dto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 异步调用，插入搜索关键词到mongodb
        ApUser user = ApUserThreadLocalUtil.getUser();
        if (user != null && dto.getFromIndex() == 0){
            // 只有获取到user以及是首页查询时候才会保存关键词，如果是翻页的则不保存
            userSearchService.insert(dto.getSearchWords(), user.getId());
        }

        // 2.设置查询条件
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 关键字分词后查询
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(dto.getSearchWords()).field("title").field("content").defaultOperator(Operator.OR);
        boolQueryBuilder.must(queryStringQueryBuilder);

        // 查询小于mindate的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime").lt(dto.getMinBehotTime().getTime());

        // 分页查询
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(dto.getPageSize());

        // 按照发布时间倒序查询
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);

        // 设置高亮 title
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color: red; font-size:inherit;'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 3.结果封装返回
        ArrayList<Map> list = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Map map = JSON.parseObject(json, Map.class);
            // 处理高亮
            if (hit.getHighlightFields() != null && hit.getHighlightFields().size() >0){
                Text[] titles = hit.getHighlightFields().get("title").getFragments();
                String title = StringUtils.join(titles);
                // 高亮标题
                map.put("h_title", title);
            }else {
                // 原始标题
                map.put("h_title", map.get("title"));
            }
            list.add(map);
        }
        return ResponseResult.okResult(list);
    }
}
