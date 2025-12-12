package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.WebsiteConfig;

/**
 * 网站配置 服务接口
 */
public interface IWebsiteConfigService {
    WebsiteConfig selectById(Long id);
    WebsiteConfig selectByKey(String configKey);
    List<WebsiteConfig> selectList(WebsiteConfig query);
    int insert(WebsiteConfig entity);
    int update(WebsiteConfig entity);
    int deleteByIds(Long[] ids);
    boolean checkKeyUnique(String configKey);
}

