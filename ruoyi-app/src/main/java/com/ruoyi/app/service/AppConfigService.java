package com.ruoyi.app.service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.WebsiteConfig;
import com.ruoyi.system.service.IWebsiteConfigService;

/**
 * APP 配置查询服务
 */
@Service
public class AppConfigService {

    @Autowired
    private IWebsiteConfigService websiteConfigService;

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

    public Map<String, List<Map<String, String>>> listAll() {
        Map<String, List<Map<String, String>>> resp = new HashMap<>();
        resp.put("app", listByGroup("app"));
        resp.put("agreement", listByGroup("agreement"));
        resp.put("customer_service", listByGroup("customer_service"));
        return resp;
    }
}

