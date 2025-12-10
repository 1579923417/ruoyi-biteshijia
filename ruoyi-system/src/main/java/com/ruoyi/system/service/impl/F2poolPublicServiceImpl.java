package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.service.F2poolPublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.Proxy;
import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class F2poolPublicServiceImpl implements F2poolPublicService {

    @Autowired
    private RestTemplate proxyRestTemplate;

    @PostConstruct
    private void init() {
        // 设置代理
        Proxy proxy = new Proxy(
                Proxy.Type.HTTP,
                new InetSocketAddress("127.0.0.1", 7897)
        );

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxy);
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(30000);

        // 这里创建一个只在本类使用的 RestTemplate
        proxyRestTemplate = new RestTemplate(factory);
    }


    @Override
    public List<Map<String, Object>> getCoinsTop100Simplified() {

        String url = "https://www.f2pool.com/coins";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Java Proxy RestTemplate)");

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        // ★ 使用带代理的 RestTemplate 发送请求
        String resp = proxyRestTemplate.postForObject(url, entity, String.class);

        System.out.println(resp);

        JSONObject obj = JSON.parseObject(resp);
        JSONObject data = obj.getJSONObject("data");
        JSONArray arr = data == null ? new JSONArray() : data.getJSONArray("top100");

        List<Map<String, Object>> list = new ArrayList<>();
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = arr.getJSONObject(i);
                Map<String, Object> m = new HashMap<>();
                m.put("marketcap", item.get("marketcap"));
                m.put("price", item.get("price"));
                m.put("volume24h", item.get("volume24h"));
                m.put("output24h", item.get("output24h"));
                m.put("network_hashrate", item.get("network_hashrate"));
                m.put("algorithm", item.get("algorithm"));
                list.add(m);
            }
        }
        return list;
    }
}
