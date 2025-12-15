package com.ruoyi.system.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.google.gson.Gson;
import com.ruoyi.system.domain.WebsiteConfig;
import com.ruoyi.system.service.ISmsService;
import com.ruoyi.system.service.IWebsiteConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class SmsServiceImpl implements ISmsService {

    @Autowired
    private IWebsiteConfigService websiteConfigService;

    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
    private String endpoint;

    private Client client;

    private String getConfig(String key) {
        WebsiteConfig config = websiteConfigService.selectByKey(key);
        if (config == null || StringUtils.isEmpty(config.getConfigValue())) {
            throw new RuntimeException("短信配置缺失：" + key);
        }
        return config.getConfigValue();
    }

    @PostConstruct
    public void init() {
        try {
            this.accessKeyId = getConfig("sms.access_key_id");
            this.accessKeySecret = getConfig("sms.access_key_secret");
            this.signName = getConfig("sms.sign_name");
            this.templateCode = getConfig("sms.template_code");
            this.endpoint = getConfig("sms.endpoint");

            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret);

            config.endpoint = endpoint;

            this.client = new Client(config);

            System.out.println("Aliyun SMS 初始化成功！");
        } catch (Exception e) {
            throw new RuntimeException("阿里云短信初始化失败", e);
        }
    }

    @Override
    public boolean sendLoginCode(String phone, String code) {
        try {
            SendSmsRequest request = new SendSmsRequest()
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setPhoneNumbers(phone)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(request);

            String respCode = response.getBody().getCode();
            return "OK".equals(respCode);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PreDestroy
    public void close() {
        System.out.println("Aliyun SMS 已关闭");
    }
}
