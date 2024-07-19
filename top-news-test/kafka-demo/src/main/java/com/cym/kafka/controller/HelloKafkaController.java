package com.cym.kafka.controller;

import com.alibaba.fastjson.JSON;
import com.cym.kafka.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloKafkaController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/hello")
    public String helloKafka(){
        User user = new User();
        user.setUsername("cym");
        user.setAge(24);
        kafkaTemplate.send("cym-topic", JSON.toJSONString(user));
        return "ok";
    }
}
