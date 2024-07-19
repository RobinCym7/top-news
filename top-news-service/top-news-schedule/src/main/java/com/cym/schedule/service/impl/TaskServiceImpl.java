package com.cym.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cym.common.constants.ScheduleConstants;
import com.cym.common.redis.CacheService;
import com.cym.model.schedule.dto.Task;
import com.cym.model.schedule.pojos.Taskinfo;
import com.cym.model.schedule.pojos.TaskinfoLogs;
import com.cym.schedule.mapper.TaskinfoLogsMapper;
import com.cym.schedule.mapper.TaskinfoMapper;
import com.cym.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Transactional
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;
    @Autowired
    private CacheService cacheService;

    /**
     * 添加延迟任务
     *
     * @param task
     * @return
     */
    @Override
    // @GlobalTransactional
    public long addTask(Task task) {
        // 1.添加任务到数据库中
        boolean success = addTaskToDb(task);
        // 2.添加任务到redis中
        if (success){
            addTaskToCache(task);
        }

        return task.getTaskId();
    }

    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    @Override
    public boolean cancelTask(long taskId) {
        boolean flag = false;
        Task task = updateDb(taskId, ScheduleConstants.CANCELLED);

        if (task != null){
            removeTaskFromCache(task);
            flag = true;
        }

        return flag;
    }

    /**
     * 拉取任务
     *
     * @param type
     * @param priority
     * @return
     */
    @Override
    public Task pullTask(int type, int priority) {

        Task task = null;

        try {
            String key = type+"_"+priority;
            String task_json = cacheService.lLeftPop(ScheduleConstants.TOPIC + key);
            if (StringUtils.isNotBlank(task_json)){
                task = JSON.parseObject(task_json, Task.class);
                // 修改数据库信息
                updateDb(task.getTaskId(), ScheduleConstants.EXECUTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("pull task exception");
        }
        return task;
    }

    /**
     * 每分钟扫描zset中的数据，将数据刷新到list中
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void refresh() {
        String token = cacheService.tryLock("FUTURE_TASK_SYNC", 1000 * 30);
        if (StringUtils.isNotBlank(token)){
            log.info("未来数据定时属性--定时任务");
            // 获取所有未来数据的集合key
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
            for (String futureKey : futureKeys) {
                // 获取当前数据的key, 变成topic
                String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];

                // 按照key和分值查询符合条件的数据
                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                // 同步数据
                if (!tasks.isEmpty()){
                    cacheService.refreshWithPipeline(futureKey, topicKey, tasks);
                    log.info("成功将"+futureKey+"刷新到了"+topicKey);
                }
            }
        }
    }

    /**
     * 数据库任务定时同步到redis
     */
    @PostConstruct // 服务重启就执行该任务
    @Scheduled(cron = "0 */5 * * * ?")
    public void reloadData() {
        // 清理缓存中的数据 list zset
        clearCache();

        // 查询符合条件的任务，小于未来五分钟的数据
        // 获取五分钟之后的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        List<Taskinfo> taskinfoList = taskinfoMapper.selectList(Wrappers.<Taskinfo>lambdaQuery().lt(Taskinfo::getExecuteTime, calendar.getTime()));

        // 把任务添加到redis中
        if (taskinfoList != null && taskinfoList.size() >0){
            for (Taskinfo taskinfo : taskinfoList) {
                Task task = new Task();
                BeanUtils.copyProperties(taskinfo, task);
                task.setExecuteTime(taskinfo.getExecuteTime().getTime());
                addTaskToCache(task);
            }
        }
        log.info("数据库中的任务同步到了redis");
    }

    /**
     * 清理缓存中的数据
     */
    private void clearCache() {
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        cacheService.delete(topicKeys);
        cacheService.delete(futureKeys);
    }

    /**
     * 删除redis中缓存的任务
     * @param task
     */
    private void removeTaskFromCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();
        if (task.getExecuteTime() <= System.currentTimeMillis()){
            cacheService.lRemove(ScheduleConstants.TOPIC+key, 0, JSON.toJSONString(task));
        }else {
            cacheService.zRemove(ScheduleConstants.FUTURE+key, JSON.toJSONString(task));
        }
    }

    /**
     * 删除任务并更新日志状态
     * @param taskId
     * @param status
     * @return
     */
    private Task updateDb(long taskId, int status) {

        Task task = null;

        try {
            // 删除任务
            taskinfoMapper.deleteById(taskId);

            // 更新任务日志
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);

            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs, task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (BeansException e) {
            log.error("task cancel exeception taskId={}", taskId);
        }
        return task;
    }

    /**
     * 将任务添加到redis中
     * @param task
     */
    private void addTaskToCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();

        // 获取五分钟之后的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();

        if (task.getExecuteTime() <= System.currentTimeMillis()){
            // 2.1如果任务的执行时间小于等于当前时间，则存入list立即执行
            cacheService.lLeftPush(ScheduleConstants.TOPIC+key, JSON.toJSONString(task));
        }else if (task.getExecuteTime() <= nextScheduleTime){
            // 2.2如果任务的执行时间大于等于当前时间，小于等于预设时间（未来五分钟）则存入zset中
            cacheService.zAdd(ScheduleConstants.FUTURE+key, JSON.toJSONString(task), task.getExecuteTime());
        }

    }

    /**
     * 将任务添加到数据库中
     * @param task
     * @return
     */
    private boolean addTaskToDb(Task task) {
        boolean flag = false;
        try {
            // 保存任务表
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);
            task.setTaskId(taskinfo.getTaskId());

            // 保存任务日志数据
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);
            flag = true;
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        return flag;
    }
}
