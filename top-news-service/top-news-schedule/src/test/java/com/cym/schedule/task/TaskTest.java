package com.cym.schedule.task;

import com.cym.model.schedule.dto.Task;
import com.cym.schedule.ScheduleApplication;
import com.cym.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskTest {
    @Autowired
    private TaskService taskService;

    @Test
    public void testAddTask(){
        Task task = new Task();
        task.setTaskType(100);
        task.setPriority(50);
        task.setParameters("task test".getBytes());
        task.setExecuteTime(new Date().getTime());
        long taskId = taskService.addTask(task);
        System.out.println(taskId);
    }

    @Test
    public void testCancelTask(){
        taskService.cancelTask(1797596188739731458L);
    }

    @Test
    public void testPullTask(){
        Task task = taskService.pullTask(100, 50);
        System.out.println(task);
    }

    @Test
    public void testScheduleTask(){
        for (int i=0; i<5; i++){
            Task task = new Task();
            task.setTaskType(100 + i);
            task.setPriority(50);
            task.setParameters("task test".getBytes());
            task.setExecuteTime(new Date().getTime()+500 * i);

            long taskId = taskService.addTask(task);
        }
    }
}
