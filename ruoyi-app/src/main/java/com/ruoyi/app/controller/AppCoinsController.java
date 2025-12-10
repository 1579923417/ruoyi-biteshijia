package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.F2poolPublicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * APP--PoW排行 前端控制器
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@RestController
@RequestMapping("/app/coins")
@Api(tags = "APP--PoW排行")
@Anonymous
public class AppCoinsController {
    @Autowired
    private F2poolPublicService f2poolPublicService;

    @Anonymous
    @ApiOperation("Top100 PoW排行")
    @PostMapping("/top100")
    public AjaxResult top100() {
        List<Map<String, Object>> list = f2poolPublicService.getCoinsTop100Simplified();
        return AjaxResult.success(list);
    }
}

