package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.app.service.AppConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APP 配置接口
 *
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@RestController
@RequestMapping("/app/config")
@Api(tags = "APP--系统配置")
public class AppConfigController {

    @Autowired
    private AppConfigService appConfigService;

    /**
     * 获取 APP 配置分组列表
     *
     * <p>
     * 返回当前 APP 可用的全部配置项，
     * 数据通常按「配置分组」进行聚合，
     * 前端可按分组直接使用或缓存到本地。
     * </p>
     *
     * @return AjaxResult 包含 APP 所需的配置分组数据
     */
    @Anonymous
    @GetMapping("/list")
    @ApiOperation("app配置分组列表")
    public AjaxResult list() {
        return AjaxResult.success(appConfigService.listAll());
    }
}

