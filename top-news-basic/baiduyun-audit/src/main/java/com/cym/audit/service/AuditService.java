package com.cym.audit.service;

import com.baidu.aip.contentcensor.EImgType;
import org.json.JSONObject;


public interface AuditService {

    /**
     * 文本内容审查
     * @param content
     * @return
     */
    JSONObject auditText(String content);

    /**
     * 图片内容审查
     * 当type为0时，path为本地路径
     * 当type为1时，path为url
     * @param path
     * @param eImgType
     * @return
     */
    JSONObject audioImage(String path, EImgType eImgType);
}
