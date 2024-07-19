package com.cym.audit.config;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.cym.audit.service.AuditService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties({BaiduYunConfigProperties.class})
@ConditionalOnClass(AuditService.class)
public class BaiDuYunConfig {

    @Autowired
    private BaiduYunConfigProperties baiduYunConfigProperties;

    @Bean
    public AipContentCensor bulidBaiduyunClient() {
        return new AipContentCensor(baiduYunConfigProperties.getAppId(), baiduYunConfigProperties.getApiKey(), baiduYunConfigProperties.getSecretKey());
    }

}
