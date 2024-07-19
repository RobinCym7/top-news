package com.cym.xxl.jobs;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DemoJob {

    @Value("${server.port}")
    private String port;

    @XxlJob("demoJobHandler")
    public void helloJob(){
        XxlJobHelper.log("xxl-JOB, hello world");
        System.out.println("简单任务执行了。。。。。"+port);
    }

}
