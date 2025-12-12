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
 * APP--排行 前端控制器
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@RestController
@RequestMapping("/app/coins")
@Api(tags = "APP--排行")
@Anonymous
public class AppCoinsController {
    @Autowired
    private F2poolPublicService f2poolPublicService;

    /**
     * 获取 Top100 PoW 排行列表
     * 返回简化版排行信息，前端可直接渲染
     *
     * @return AjaxResult 返回 Top100 排行列表
     */
    @Anonymous
    @ApiOperation("Top100 PoW排行")
    @PostMapping("/top100")
    public AjaxResult top100() {
        List<Map<String, Object>> list = f2poolPublicService.getCoinsTop100Simplified();
        return AjaxResult.success(list);
    }

    /**
     * 获取矿机列表（简化字段）
     * 返回字段：id、name、display_currency_code、hashrate_unit_value、power、coins_24h、company_icon
     * 前端无需传参
     */
    @Anonymous
    @ApiOperation("矿机信息排行")
    @PostMapping("/miners")
    public AjaxResult miners() {
        List<Map<String, Object>> list = f2poolPublicService.getMinersSimplified();
        return AjaxResult.success(list);
    }
}
