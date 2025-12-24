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
 *
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
     * 获取 PoW 币种 Top100 排行
     *
     * 返回当前主流 PoW 币种的 Top100 排行列表，
     * 服务层已对第三方返回数据进行精简处理，
     * 前端无需再次做复杂字段解析。
     *
     * 返回结果为 List&lt;Map&gt; 结构，
     * 主要用于 APP 首页或排行页面展示。
     *
     * @return AjaxResult 包含 PoW Top100 排行数据
     */
    @Anonymous
    @ApiOperation("Top100 PoW排行")
    @PostMapping("/top100")
    public AjaxResult top100() {
        List<Map<String, Object>> list = f2poolPublicService.getCoinsTop100Simplified();
        return AjaxResult.success(list);
    }

    /**
     * 获取矿机排行信息（简化字段）
     *
     * 返回当前主流矿机的排行信息，
     * 数据已在服务层进行裁剪，仅保留前端展示所需字段，
     *
     * 包括：
     *   id：矿机ID
     *   name：矿机名称
     *   display_currency_code：展示币种
     *   hashrate_unit_value：算力值
     *   power：功耗
     *   coins_24h：24小时收益
     *   company_icon：厂商图标
     *
     * 前端无需传入任何参数，
     * 可直接用于排行列表或卡片式展示。
     *
     * @return AjaxResult 包含矿机排行信息列表
     */
    @Anonymous
    @ApiOperation("矿机信息排行")
    @PostMapping("/miners")
    public AjaxResult miners() {
        List<Map<String, Object>> list = f2poolPublicService.getMinersSimplified();
        return AjaxResult.success(list);
    }
}
