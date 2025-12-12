package com.ruoyi.system.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.google.gson.Gson;
import com.ruoyi.system.service.ISmsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class SmsServiceImpl implements ISmsService {



    private Client client;

    @PostConstruct
    public void init() {
        try {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret);

            config.endpoint = endpoint;

            this.client = new Client(config);

            System.out.println("Aliyun SMS 初始化成功！");
        } catch (Exception e) {
            e.printStackTrace();
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

            System.out.println("短信返回：" + new Gson().toJson(response));

            String msg = response.getBody().getMessage();
            String respCode = response.getBody().getCode();

            // 阿里短信成功条件：Code = OK
            return "OK".equals(msg) || "OK".equals(respCode);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PreDestroy
    public void close() {
        // Tea SDK 在新版中 client 不需要显式关闭
        System.out.println("Aliyun SMS 已关闭");
    }
}
