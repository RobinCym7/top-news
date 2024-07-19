package com.cym.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cym.apis.user.IUserClient;
import com.cym.article.mapper.ApArticleConfigMapper;
import com.cym.article.mapper.ArticleContentMapper;
import com.cym.article.mapper.ArticleMapper;
import com.cym.article.service.ArticleFreemarkerService;
import com.cym.article.service.ArticleService;
import com.cym.common.constants.ArticleConstants;
import com.cym.common.redis.CacheService;
import com.cym.model.article.dto.ArticleBehaviorDto;
import com.cym.model.article.dto.ArticleDto;
import com.cym.model.article.dto.ArticleHomeDto;
import com.cym.model.article.pojos.ApArticle;
import com.cym.model.article.pojos.ApArticleConfig;
import com.cym.model.article.pojos.ApArticleContent;
import com.cym.model.article.vo.HotArticleVo;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.AppHttpCodeEnum;
import com.cym.model.user.dtos.UserFollowDto;
import com.cym.utils.thread.ApUserThreadLocalUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ApArticle> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ApArticleConfigMapper articleConfigMapper;
    @Autowired
    private ArticleContentMapper articleContentMapper;
    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private IUserClient userClient;

    /**
     * 加载文章接口
     * @param dto
     * @param loadType
     * @return
     */
    @Override
    public ResponseResult load(ArticleHomeDto dto, Short loadType) {
        // 1.判断参数的合法性
        if (!loadType.equals(ArticleConstants.LOADTYPE_LOAD_MORE) || !loadType.equals(ArticleConstants.LOADTYPE_LOAD_NEW)){
            // 1.1 如果传来的loadType非法，则赋为默认值
            loadType = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        Integer size = dto.getSize();
        if (size==null || size==0){
            // 1.2 如果传来的文章size有误
            size = 10;
        }
        // 1.3 文章size不超过50
        size = Math.min(50, size);
        if (StringUtils.isBlank(dto.getTag())){
            // 1.4 如果传来的文章标签为空，则赋默认值
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }
        // 1.5 时间校验
        if (dto.getMaxBehotTime() == null){dto.setMaxBehotTime(new Date());}
        if (dto.getMinBehotTime() == null){dto.setMinBehotTime(new Date());}

        // 2. 参数合法，查询文章
        List<ApArticle> articleList = articleMapper.loadArticle(dto, loadType);

        return ResponseResult.okResult(articleList);
    }

    /**
     * 文章保存接口
     * @param dto
     * @return
     */
    @Override
    // @GlobalTransactional
    public ResponseResult saveArticle(ArticleDto dto) {
        // 1.检查参数
        if(dto ==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        // 2.判断id是否存在
        if (dto.getId() == null){
            // 2.1不存在， 保存文章、文章配置、文章内容
            // 2.1.1 保存文章
            System.err.println("-=-=-=-=-=-=-=-=-=");
            System.out.println(apArticle);
            save(apArticle);

            // 2.1.2 保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            System.err.println("=========================");
            System.err.println(apArticleConfig);
            System.err.println("=============================");
            articleConfigMapper.insert(apArticleConfig);

            // 2.1.3保存文章内容
            ApArticleContent articleContent = new ApArticleContent();
            articleContent.setArticleId(apArticle.getId());
            articleContent.setContent(dto.getContent());
            articleContentMapper.insert(articleContent);
        }else {
            // 2.2存在id，修改文章、文章内容
            // 2.2.1修改文章
            updateById(apArticle);

            // 2.2.2修改文章内容
            ApArticleContent articleContent = articleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            articleContent.setContent(dto.getContent());
            articleContentMapper.updateById(articleContent);
        }

        // 异步调用，生成静态文件上传到MinIO中
        articleFreemarkerService.buildArticleToMinIO(apArticle, dto.getContent());

        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 根据articleId获取文章
     *
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult getArticle(Long articleId) {
        ApArticle article = getById(articleId);
        if (article == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.ARTICLE_NOT_EXIST);
        }
        return ResponseResult.okResult(JSON.toJSONString(article));
    }

    /**
     * 文章行为加载
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult articleBehavior(ArticleBehaviorDto dto) {
        // 1.参数验证
        if (dto ==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        String key = "User.Likes" + ApUserThreadLocalUtil.getUser().getId();
        // 2.查询是否点赞
        List<String> articleList = cacheService.lRange(key, 0, -1);
        System.err.println(articleList);
        boolean contains = false;
        if (articleList.size() > 0){
            contains = articleList.contains(dto.getArticleId().toString());
        }

        boolean is_like = false;
        if (contains){
            is_like = true;
        }
        boolean isunlike = !is_like;

        String collection_key = "user.collection."+ApUserThreadLocalUtil.getUser().getId();
        List<String> collection_list = cacheService.lRange(collection_key, 0, -1);
        boolean iscollection = false;
        System.out.println(collection_list);
        System.out.println(collection_list.size());
        System.out.println(dto.getArticleId());
        if (collection_list.size() > 0){
            iscollection = collection_list.contains(dto.getArticleId().toString());
        }

        UserFollowDto userFollowDto = new UserFollowDto();
        userFollowDto.setUserId(ApUserThreadLocalUtil.getUser().getId());
        userFollowDto.setAuthorId(dto.getAuthorId());
        boolean isfollow = userClient.isFollow(userFollowDto);
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("islike", is_like);
        map.put("isunlike", isunlike);
        map.put("iscollection", iscollection);
        map.put("isfollow", isfollow);
        return ResponseResult.okResult(map);
    }

    /**
     * 加载文章列表
     *
     * @param dto
     * @param type      1 加载更多   2 加载最新
     * @param firstPage true  是首页  flase 非首页
     * @return
     */
    @Override
    public ResponseResult load2(ArticleHomeDto dto, Short type, boolean firstPage) {
        if(firstPage){
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if(StringUtils.isNotBlank(jsonStr)){
                List<HotArticleVo> hotArticleVoList = JSON.parseArray(jsonStr, HotArticleVo.class);
                ResponseResult responseResult = ResponseResult.okResult(hotArticleVoList);
                return responseResult;
            }
        }
        return load(dto, type);
    }
}
