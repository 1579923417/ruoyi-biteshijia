package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.WebsiteConfig;

/**
 * 网站配置 Mapper
 */
public interface WebsiteConfigMapper {
    WebsiteConfig selectById(Long id);
    WebsiteConfig selectByKey(String configKey);
    List<WebsiteConfig> selectList(WebsiteConfig query);
    int insert(WebsiteConfig entity);
    int update(WebsiteConfig entity);
    int deleteByIds(Long[] ids);
}

