package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.GlobalPowerInfo;
import com.ruoyi.system.domain.MinerFarm;
import com.ruoyi.system.domain.vo.GlobalPowerInfoVo;
import com.ruoyi.system.domain.vo.MinerFarmVo;
import com.ruoyi.system.service.IGlobalPowerInfoService;
import com.ruoyi.system.service.IMinerFarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * APP--全球信息 前端控制器
 *
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@RestController
@RequestMapping("/app/global")
@Api(tags = "APP--全球信息")
@Anonymous
public class AppGlobalController {

    @Autowired
    private IGlobalPowerInfoService globalPowerInfoService;

    @Autowired
    private IMinerFarmService minerFarmService;

    /**
     * 获取全球电力信息列表
     *
     * 返回当前系统中维护的全球电力相关数据，
     * 如地区电力成本、电力来源说明等（具体字段以 VO 为准）。
     *
     * 接口不接收前端参数，直接返回全部可展示数据，
     * 适用于 APP 端的静态或半静态内容展示。
     *
     * @return AjaxResult 包含全球电力信息 VO 列表
     */
    @ApiOperation("全球电力信息列表")
    @PostMapping("/power")
    public AjaxResult power() {
        GlobalPowerInfo query = new GlobalPowerInfo();
        List<GlobalPowerInfoVo> vos = globalPowerInfoService.selectVoList(query);
        return AjaxResult.success(vos);
    }

    /**
     * 获取全球矿场信息列表
     *
     * 返回系统中配置的全球矿场相关信息，
     * 包括矿场基础介绍、地理位置、规模说明等内容（以 VO 为准）。
     *
     * 接口不依赖用户登录态，
     * 主要用于 APP 端全球矿场展示、科普或宣传页面。
     *
     * @return AjaxResult 包含全球矿场信息 VO 列表
     */
    @ApiOperation("全球矿场信息列表")
    @PostMapping("/miner")
    public AjaxResult miner() {
        MinerFarm query = new MinerFarm();
        List<MinerFarmVo> vos = minerFarmService.selectVoList(query);
        return AjaxResult.success(vos);
    }
}

