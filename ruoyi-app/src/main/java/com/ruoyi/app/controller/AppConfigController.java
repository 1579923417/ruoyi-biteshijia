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
 */
@RestController
@RequestMapping("/app/config")
@Api(tags = "APP--系统配置")
public class AppConfigController {

    @Autowired
    private AppConfigService appConfigService;

    @Anonymous
    @GetMapping("/list")
    @ApiOperation("app配置分组列表")
    public AjaxResult list() {
        return AjaxResult.success(appConfigService.listAll());
    }
}

