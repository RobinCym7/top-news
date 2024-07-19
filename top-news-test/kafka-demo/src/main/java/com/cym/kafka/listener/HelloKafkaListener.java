package com.cym.kafka.listener;

import com.alibaba.fastjson.JSON;
import com.cym.kafka.pojo.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HelloKafkaListener {

    @KafkaListener(topics = {"cym-topic"})
    public void listenMessage(String msg){
        if (!StringUtils.isEmpty(msg)){
            User user = JSON.parseObject(msg, User.class);
            System.out.println(user);
        }

    }

}
