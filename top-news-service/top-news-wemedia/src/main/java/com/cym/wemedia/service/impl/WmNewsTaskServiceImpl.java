package com.cym.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.cym.apis.schedule.IScheduleClient;
import com.cym.model.common.dtos.ResponseResult;
import com.cym.model.common.enums.TaskTypeEnum;
import com.cym.model.schedule.dto.Task;
import com.cym.model.wemedia.pojos.WmNews;
import com.cym.wemedia.service.WmNewsAutoScanService;
import com.cym.utils.common.ProtostuffUtil;
import com.cym.wemedia.service.WmNewsTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class WmNewsTaskServiceImpl implements WmNewsTaskService {

    @Autowired
    private IScheduleClient scheduleClient;
    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 添加任务到延迟队列中
     *
     * @param id          文章的id
     * @param publishTime 发布时间，可以作为任务的执行时间
     */
    @Override
    @Async
    public void addNewsToTask(Integer id, Date publishTime) {
        log.info("添加任务到延迟服务中--begin--");
        Task task = new Task();
        task.setExecuteTime(publishTime.getTime());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        WmNews wmNews = new WmNews();
        wmNews.setId(id);
        task.setParameters(ProtostuffUtil.serialize(wmNews));
        scheduleClient.addTask(task);
        log.info("添加任务到延迟服务中--end--");
    }

    /**
     * 消费任务，审核文章
     */
    @Scheduled(fixedRate = 1000) // 每秒钟拉取一次
    @Override
    public void scanNewsByTask() {
        ResponseResult responseResult = scheduleClient.pullTask(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType(), TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        if (responseResult.getCode().equals(200) && responseResult.getData() != null){
            Task task = JSON.parseObject(JSON.toJSONString(responseResult.getData()), Task.class);
            WmNews wmNews = ProtostuffUtil.deserialize(task.getParameters(), WmNews.class);
            wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
            log.info("已审核文章：{}", wmNews.getId());
        }
    }
}
