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
     * 全球电力信息列表
     * @return AjaxResult 列表数据
     */
    @ApiOperation("全球电力信息列表")
    @PostMapping("/power")
    public AjaxResult power() {
        GlobalPowerInfo query = new GlobalPowerInfo();
        List<GlobalPowerInfoVo> vos = globalPowerInfoService.selectVoList(query);
        return AjaxResult.success(vos);
    }

    /**
     * 全球矿场信息列表
     * @return AjaxResult 列表数据
     */
    @ApiOperation("全球矿场信息列表")
    @PostMapping("/miner")
    public AjaxResult miner() {
        MinerFarm query = new MinerFarm();
        List<MinerFarmVo> vos = minerFarmService.selectVoList(query);
        return AjaxResult.success(vos);
    }
}

