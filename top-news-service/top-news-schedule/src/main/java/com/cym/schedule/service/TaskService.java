package com.cym.schedule.service;

import com.cym.model.schedule.dto.Task;

public interface TaskService {

    /**
     * 添加延迟任务
     * @param task
     * @return
     */
    public long addTask(Task task);

    /**
     * 取消任务
     * @param taskId
     * @return
     */
    public boolean cancelTask(long taskId);

    /**
     * 拉取任务
     * @param type
     * @param priority
     * @return
     */
    public Task pullTask(int type, int priority);
}
