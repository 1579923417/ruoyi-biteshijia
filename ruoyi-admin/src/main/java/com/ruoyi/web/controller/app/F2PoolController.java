package com.ruoyi.web.controller.app;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.F2PoolService;
import java.util.List;
import java.util.Map;

/**
 * 第三方接口--f2pool 前端控制器
 */
@RestController
@RequestMapping("/admin/f2pool")
@Api(tags = "f2pool")
@Anonymous
public class F2PoolController {

    @Autowired
    private F2PoolService f2PoolService;

    @ApiOperation(value = "同步矿机数据")
    @PostMapping("/sync/miner")
    public AjaxResult syncMinerData() {
        f2PoolService.syncMinerData();
        return AjaxResult.success("矿机数据同步完成");
    }

    //====================帐户管理====================
    @ApiOperation(value = "获取子帐户信息")
    @PostMapping("/v2/mining_user/get")
    public AjaxResult getMiningUser(@RequestParam("username") String username,
                                    @RequestParam("mining_user_name") String miningUserName) {
        JSONObject data = f2PoolService.getMiningUser(username, miningUserName);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "添加子帐户")
    @PostMapping("/v2/mining_user/add")
    public AjaxResult addMiningUser(@RequestParam("username") String username,
                                    @RequestParam("mining_user_name") String miningUserName) {
        JSONObject data = f2PoolService.addMiningUser(username, miningUserName);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "获取子帐户列表")
    @PostMapping("/v2/mining_user/list")
    public AjaxResult listMiningUsers(@RequestParam("username") String username) {
        JSONObject data = f2PoolService.listMiningUsers(username);
        return AjaxResult.success(data);
    }

    //====================资产情况====================
    @ApiOperation(value = "获取用户当前资产信息")
    @PostMapping("/v2/assets/balance")
    public AjaxResult getUserAssets(@RequestParam("username") String username,
                                    @RequestParam("currency") String currency,
                                    @RequestParam("mining_user_name") String miningUserName,
                                    @RequestParam("address") String address,
                                    @RequestParam("calculate_estimated_income") Boolean calculateEstimatedIncome,
                                    @RequestParam("historical_total_income_outcome") Boolean historicalTotalIncomeOutcome){
        JSONObject data = f2PoolService.getUserAssets(username, currency, miningUserName, address, calculateEstimatedIncome, historicalTotalIncomeOutcome);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "收支流水账单")
    @PostMapping("/v2/assets/transactions/list")
    public AjaxResult listTransactionHistory(@RequestParam("username") String username,
                                             @RequestParam("currency") String currency,
                                             @RequestParam("mining_user_name") String miningUserName,
                                             @RequestParam("address") String address,
                                             @RequestParam("type") String type,
                                             @RequestParam("start_time") Integer startTime,
                                             @RequestParam("end_time") Integer endTime){
        JSONObject data = f2PoolService.listTransactionHistory(username, currency, miningUserName, address, type, startTime, endTime);
        return AjaxResult.success(data);
    }

    //====================算力情况====================
    @ApiOperation(value = "算力数据")
    @PostMapping("/v2/hash_rate/info")
    public AjaxResult getHashRate(@RequestParam("username") String username,
                                  @RequestParam("mining_user_name") String miningUserName,
                                  @RequestParam("address") String address,
                                  @RequestParam("currency") String currency){
        JSONObject data = f2PoolService.getHashRate(username, miningUserName, address, currency);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "同时查询多帐户的算力数据")
    @PostMapping("/v2/hash_rate/info_list")
    public AjaxResult getHashRateList(@RequestParam("username") String username,
                                      @RequestBody List<Map<String, Object>> reqs){
        JSONObject data = f2PoolService.getHashRateList(username, reqs);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "矿工列表")
    @PostMapping("/v2/hash_rate/worker/list")
    public AjaxResult listWorkers(@RequestParam("username") String username,
                                  @RequestParam("mining_user_name") String miningUserName,
                                  @RequestParam("address") String address,
                                  @RequestParam("currency") String currency){
        JSONObject data = f2PoolService.listWorkers(username, miningUserName, address, currency);
        return AjaxResult.success(data);
    }
}