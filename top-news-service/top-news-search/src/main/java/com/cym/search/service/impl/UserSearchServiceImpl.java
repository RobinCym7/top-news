package com.cym.search.service.impl;

import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.search.dtos.HistorySearchDto;
import com.cym.model.search.dtos.UserSearchDto;
import com.cym.model.user.pojos.ApUser;
import com.cym.search.pojos.ApAssociateWords;
import com.cym.search.pojos.ApUserSearch;
import com.cym.search.service.UserSearchService;
import com.cym.utils.thread.ApUserThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserSearchServiceImpl implements UserSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 插入历史记录到mongodb
     * @param keyword
     * @param userId
     */
    @Override
    public void insert(String keyword, Integer userId) {
        // 1.查询当前用户的搜索关键词
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword));
        ApUserSearch apUserSearch = mongoTemplate.findOne(query, ApUserSearch.class);

        // 2.存在  更新创建时间
        if (apUserSearch != null){
            apUserSearch.setCreatedTime(new Date());
            mongoTemplate.save(apUserSearch);
            return;
        }

        // 3. 不存在，判断当前历史记录总数是否超过10条
        apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(userId);
        apUserSearch.setKeyword(keyword);
        apUserSearch.setCreatedTime(new Date());

        Query query1 = Query.query(Criteria.where("userId").is(userId));
        query1.with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<ApUserSearch> userSearchList = mongoTemplate.find(query1, ApUserSearch.class);
        if (userSearchList ==null || userSearchList.size() < 10){
            mongoTemplate.save(apUserSearch);
        }else {
            ApUserSearch lastUserSearch = userSearchList.get(userSearchList.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearch.getId())), apUserSearch);
        }
    }

    /**
     * 搜索历史记录
     *
     * @return
     */
    @Override
    public ResponseResult load() {

        ApUser user = ApUserThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        List<ApUserSearch> apUserSearchList = mongoTemplate.find(Query.query(Criteria.where("userId").is(user.getId())).with(Sort.by(Sort.Direction.DESC, "createdTime")), ApUserSearch.class);
        return ResponseResult.okResult(apUserSearchList);

    }

    /**
     * 删除搜索记录
     * @param dto
     * @return
     */
    @Override
    public ResponseResult deleteSearch(HistorySearchDto dto) {
        // 获取用户
        ApUser user = ApUserThreadLocalUtil.getUser();

        // 1.判断是否获取到用户
        if (user == null){
            // 1.1用户不存在，需要登录
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2.判断参数是否合法
        if (dto.getId() == null && StringUtils.isBlank(dto.getId())){
            // 2.1参数不合法
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //3.删除
        mongoTemplate.remove(Query.query(Criteria.where("userId").is(user.getId()).and("id").is(dto.getId())),ApUserSearch.class);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 用户搜索联想词
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult associateSearch(UserSearchDto dto) {
        // 获取用户
        ApUser user = ApUserThreadLocalUtil.getUser();

        // 1.判断是否获取到用户
        if (user == null){
            // 1.1用户不存在，需要登录
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2.检查参数
        if (StringUtils.isBlank(dto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 3.分页查询
        if (dto.getPageSize() > 20) {
            dto.setPageSize(20);
        }

        // 4.执行查询，模糊查询
        Query query = Query.query(Criteria.where("associateWords").regex(".*?\\" + dto.getSearchWords() + ".*"));
        query.limit(dto.getPageSize());
        List<ApAssociateWords> apAssociateWords = mongoTemplate.find(query, ApAssociateWords.class);
        return ResponseResult.okResult(apAssociateWords);
    }
}
