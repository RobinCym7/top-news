package com.cym.audit.service.impl;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import com.cym.audit.config.BaiDuYunConfig;
import com.cym.audit.config.BaiduYunConfigProperties;
import com.cym.audit.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Slf4j
@EnableConfigurationProperties(BaiduYunConfigProperties.class)
@Import(BaiDuYunConfig.class)
public class BaiDuYunAuditService implements AuditService {

    @Autowired
    private AipContentCensor client;

    @Override
    public JSONObject auditText(String content) {
        return client.textCensorUserDefined(content);
    }

    /**
     * 图片内容审查
     * 当type为0时，path为本地路径
     * 当type为1时，path为url
     * @param path
     * @param eImgType
     * @return
     */
    @Override
    public JSONObject audioImage(String path, EImgType eImgType) {
        return client.imageCensorUserDefined(path, eImgType, null);
    }
}
