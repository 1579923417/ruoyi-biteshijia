package com.ruoyi.system.factory;

import com.ruoyi.system.domain.WebsiteConfig;
import com.ruoyi.system.service.IWebsiteConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.Proxy;
import java.net.InetSocketAddress;

/**
 * HTTP 代理工厂
 *
 * 负责从动态配置（websiteconfig 表）中读取代理相关配置，
 * 并统一构建实例，供系统内 HTTP 请求使用。
 *
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@Component
public class ProxyFactory {

    @Autowired
    private IWebsiteConfigService websiteConfigService;

    public Proxy buildHttpProxy() {
        WebsiteConfig ipCfg = websiteConfigService.selectByKey("proxy.ip");
        WebsiteConfig portCfg = websiteConfigService.selectByKey("proxy.port");

        if (ipCfg == null || portCfg == null) {
            return Proxy.NO_PROXY;
        }

        String ip = ipCfg.getConfigValue();
        String port = portCfg.getConfigValue();

        if (StringUtils.isEmpty(ip) || StringUtils.isEmpty(port)) {
            return Proxy.NO_PROXY;
        }

        return new Proxy(
                Proxy.Type.HTTP,
                new InetSocketAddress(ip, Integer.parseInt(port))
        );
    }
}
