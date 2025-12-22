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
 * APP -- F2Pool 公共数据业务实现
 *
 * 设计说明：
 * 1. 该服务用于获取 F2Pool 官网公开展示的数据（排行榜、矿机信息等）
 * 2. 目标数据原本面向浏览器访问，后端直连存在反爬与地域限制
 * 3. 因此统一通过 HTTP 代理 + RestTemplate 模拟浏览器请求
 *
 * 注意事项：
 * - 当前接口返回结构可能随官网调整而变化
 * - 本服务只做“数据抽取与简化”，不保证字段长期稳定
 *
 * @author Jamie
 * @date 2025/12/10
 */
@Service
public class F2poolPublicServiceImpl implements F2poolPublicService {

        /**
     * 代理工厂
     *
     * 用途：
     * - 构建 HTTP Proxy
     * - 解决部分海外网站的访问限制 / 网络策略问题
     */
    @Autowired
    private ProxyFactory proxyFactory;

    /**
     * 带代理的 RestTemplate
     *
     * 说明：
     * - 专用于访问第三方站点（如 f2pool.com）
     * - 与系统内普通 RestTemplate 隔离，避免影响内部接口调用
     */
    private RestTemplate proxyRestTemplate;

    /**
     * 初始化代理 RestTemplate
     *
     * 执行时机：
     * - Spring Bean 初始化完成后执行一次
     *
     * 配置说明：
     * - 设置 HTTP 代理
     * - 设置连接超时 / 读取超时，避免线程长时间阻塞
     */
    @PostConstruct
    private void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxyFactory.buildHttpProxy());
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(30000);
        this.proxyRestTemplate = new RestTemplate(factory);
    }

    /**
     * 获取 F2Pool Top100 币种简化数据
     *
     * 数据来源：
     * - F2Pool 官网 /coins 页面对应接口
     *
     * 处理逻辑：
     * 1. 通过代理 + RestTemplate 发送 HTTP 请求
     * 2. 模拟浏览器请求头（User-Agent）
     * 3. 解析返回 JSON
     * 4. 仅提取前端展示所需的核心字段
     *
     * @return Top100 币种的简化列表
     */
    @Override
    public List<Map<String, Object>> getCoinsTop100Simplified() {

        String url = "https://www.f2pool.com/coins";
        // 构造请求头，模拟浏览器访问，降低被反爬概率
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Java Proxy RestTemplate)");
        // F2Pool 接口为 POST 请求，即使 body 实际未使用，也需传空 JSON
        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        // 发起请求并获取原始响应
        String resp = proxyRestTemplate.postForObject(url, entity, String.class);
        // 解析 JSON
        JSONObject obj = JSON.parseObject(resp);
        JSONObject data = obj.getJSONObject("data");
        JSONArray arr = data == null ? new JSONArray() : data.getJSONArray("top100");
        // 将原始数据转换为前端友好的 Map 结构
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
                m.put("display_currency_code", item.get("display_currency_code"));
                list.add(m);
            }
        }
        return list;
    }

    /**
     * 获取 F2Pool 矿机信息简化列表
     *
     * 数据来源：
     * - F2Pool 官网 /miners 页面对应接口
     *
     * 兼容性说明：
     * - 不同时间段返回的 data 结构可能不同
     * - data 可能是：
     *   1. JSONArray
     *   2. JSONObject（内部包含 miners / list 字段）
     *
     * 该方法通过多分支解析，提升接口容错能力
     *
     * @return 矿机简化信息列表
     */
    @Override
    public List<Map<String, Object>> getMinersSimplified() {
        String url = "https://www.f2pool.com/miners";
        // 构造请求头，模拟浏览器请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Java Proxy RestTemplate)");

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);
        String resp = proxyRestTemplate.postForObject(url, entity, String.class);

        JSONObject obj = JSON.parseObject(resp);
        Object dataNode = obj.get("data");
        JSONArray arr;

        // data 为数组的情况
        if (dataNode instanceof JSONArray) {
            arr = (JSONArray) dataNode;
        // data 为对象，内部再嵌套 miners / list
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

