package com.cym.audit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "baiduyun") //百度云配置
public class BaiduYunConfigProperties {

    //设置APPID/AK/SK
    public String appId;
    public String apiKey;
    public String secretKey;

}
