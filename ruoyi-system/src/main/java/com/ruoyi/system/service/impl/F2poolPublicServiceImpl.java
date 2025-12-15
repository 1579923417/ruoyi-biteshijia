package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.factory.ProxyFactory;
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

/**
 * APP--排行 业务层处理
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@Service
public class F2poolPublicServiceImpl implements F2poolPublicService {

    @Autowired
    private ProxyFactory proxyFactory;

    private RestTemplate proxyRestTemplate;

    @PostConstruct
    private void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxyFactory.buildHttpProxy());
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(30000);
        this.proxyRestTemplate = new RestTemplate(factory);
    }

    @Override
    public List<Map<String, Object>> getCoinsTop100Simplified() {

        String url = "https://www.f2pool.com/coins";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Java Proxy RestTemplate)");

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        String resp = proxyRestTemplate.postForObject(url, entity, String.class);

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

    @Override
    public List<Map<String, Object>> getMinersSimplified() {
        String url = "https://www.f2pool.com/miners";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Java Proxy RestTemplate)");

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        String resp = proxyRestTemplate.postForObject(url, entity, String.class);

        JSONObject obj = JSON.parseObject(resp);
        Object dataNode = obj.get("data");
        JSONArray arr;
        if (dataNode instanceof JSONArray) {
            arr = (JSONArray) dataNode;
        } else if (dataNode instanceof JSONObject) {
            JSONObject data = (JSONObject) dataNode;
            arr = data.getJSONArray("miners");
            if (arr == null) arr = data.getJSONArray("list");
        } else {
            arr = new JSONArray();
        }

        List<Map<String, Object>> list = new ArrayList<>();
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = arr.getJSONObject(i);
                Map<String, Object> m = new HashMap<>();
                m.put("id", item.get("id"));
                m.put("name", item.get("name"));
                m.put("display_currency_code", item.get("display_currency_code"));
                m.put("hashrate_unit_value", item.get("hashrate_unit_value"));
                m.put("power", item.get("power"));
                m.put("coins_24h", item.get("coins_24h"));
                m.put("company_icon", item.get("company_icon"));
                list.add(m);
            }
        }
        return list;
    }
}

