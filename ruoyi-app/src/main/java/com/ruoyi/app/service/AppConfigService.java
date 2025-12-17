package com.ruoyi.app.service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.WebsiteConfig;
import com.ruoyi.system.service.IWebsiteConfigService;

/**
 * APP 配置查询服务
 *
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@Service
public class AppConfigService {

    @Autowired
    private IWebsiteConfigService websiteConfigService;

    /**
     * 根据配置分组查询配置项列表
     *
     * <p>
     * 将数据库中的配置项转换为前端可直接使用的结构：
     * key-value-type-desc
     * </p>
     *
     * @param group 配置分组标识（如 app、agreement、customer_service）
     * @return 配置项列表，若无数据返回空集合
     */
    private List<Map<String, String>> listByGroup(String group) {
        WebsiteConfig query = new WebsiteConfig();
        query.setConfigGroup(group);
        List<WebsiteConfig> list = websiteConfigService.selectList(query);
        return list == null ? Collections.emptyList() : list.stream().map(item -> {
            Map<String, String> m = new HashMap<>();
            m.put("key", item.getConfigKey());
            m.put("value", item.getConfigValue());
            m.put("type", item.getConfigType());
            m.put("desc", item.getDescription());
            return m;
        }).collect(Collectors.toList());
    }

    /**
     * 获取 APP 所需的全部配置分组
     *
     * <p>
     * 返回结构示例：
     * <pre>
     * {
     *   "app": [...],
     *   "agreement": [...],
     *   "customer_service": [...]
     * }
     * </pre>
     * </p>
     *
     * <p>
     * 供 APP 启动或设置页面一次性拉取所有配置使用。
     * </p>
     *
     * @return 按分组组织的配置数据
     */
    public Map<String, List<Map<String, String>>> listAll() {
        Map<String, List<Map<String, String>>> resp = new HashMap<>();
        resp.put("app", listByGroup("app"));
        resp.put("agreement", listByGroup("agreement"));
        resp.put("customer_service", listByGroup("customer_service"));
        return resp;
    }
}

