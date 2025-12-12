package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.WebsiteConfig;
import com.ruoyi.system.mapper.WebsiteConfigMapper;
import com.ruoyi.system.service.IWebsiteConfigService;

/**
 * 网站配置 服务实现
 */
@Service
public class WebsiteConfigServiceImpl implements IWebsiteConfigService {

    @Autowired
    private WebsiteConfigMapper mapper;

    public WebsiteConfig selectById(Long id) { return mapper.selectById(id); }
    public WebsiteConfig selectByKey(String configKey) { return mapper.selectByKey(configKey); }
    public List<WebsiteConfig> selectList(WebsiteConfig query) { return mapper.selectList(query); }
    public int insert(WebsiteConfig entity) { return mapper.insert(entity); }
    public int update(WebsiteConfig entity) { return mapper.update(entity); }
    public int deleteByIds(Long[] ids) { return mapper.deleteByIds(ids); }
    public boolean checkKeyUnique(String configKey) { return mapper.selectByKey(configKey) == null; }
}

