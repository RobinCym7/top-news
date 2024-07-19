package com.cym.wemedia;

import com.baidu.aip.contentcensor.EImgType;
import com.cym.audit.service.AuditService;
import com.cym.audit.service.impl.BaiDuYunAuditService;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BaiDuYunTest {

    @Autowired
    private BaiDuYunAuditService baiduyunAuditService;

    @Test
    public void testImageAudit(){
        String url = "http://www.sichuanpeace.gov.cn/sccaw/u_yhscwj/202106/151712456n7y.jpg";
        JSONObject jsonObject = baiduyunAuditService.audioImage(url, EImgType.URL);
        System.out.println(jsonObject);
        System.out.println(jsonObject.get("conclusion"));
    }

    @Test
    public void testTextAudit(){
        String context = "测试内容；测试内容；测试内容；测试内容；测试内容；测试内容；请在这里输入正文";
        JSONObject jsonObject = baiduyunAuditService.auditText(context);
        System.out.println(jsonObject);
    }

}
