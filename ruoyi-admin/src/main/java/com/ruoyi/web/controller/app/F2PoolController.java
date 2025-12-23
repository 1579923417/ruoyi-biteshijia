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
 * 【第三方接口】F2Pool 相关功能控制器
 *
 * 主要职责：
 * 1. 对外提供 F2Pool 相关接口的 HTTP 访问入口
 * 2. 接收前端 / 管理后台请求参数
 * 3. 调用 F2PoolService 处理具体业务逻辑
 * 4. 统一返回 AjaxResult 结构
 *
 * 使用场景：
 * - 管理后台手动操作 F2Pool 相关功能
 * - 定时任务 / 管理接口触发矿机同步
 *
 * 说明：
 * - 当前接口标记为 @Anonymous，不做登录校验（根据业务可调整）
 */
@RestController
@RequestMapping("/admin/f2pool")
@Api(tags = "f2pool")
@Anonymous
public class F2PoolController {

    @Autowired
    private F2PoolService f2PoolService;

    /**
     * 同步所有用户的矿机数据
     *
     * 功能说明：
     * - 遍历系统内所有已配置 F2Pool Token 的用户
     * - 调用 F2Pool API 拉取矿机及收益数据
     * - 同步更新本地矿机表（app_user_miner）
     *
     * 使用场景：
     * - 后台手动点击同步
     * - 定时任务触发同步（Quartz / Spring Scheduler）
     */
    @ApiOperation(value = "同步矿机数据")
    @PostMapping("/sync/miner")
    public AjaxResult syncMinerData() {
        f2PoolService.syncMinerData();
        return AjaxResult.success("矿机数据同步完成");
    }

    //====================帐户管理====================

    /**
     * 获取指定子账户信息
     *
     * @param username          F2Pool 主账户名
     * @param miningUserName    子账户名称（矿工用户名）
     */
    @ApiOperation(value = "获取子帐户信息")
    @PostMapping("/v2/mining_user/get")
    public AjaxResult getMiningUser(@RequestParam("username") String username,
                                    @RequestParam("mining_user_name") String miningUserName) {
        JSONObject data = f2PoolService.getMiningUser(username, miningUserName);
        return AjaxResult.success(data);
    }

    /**
     * 新增子账户（矿工账户）
     *
     * @param username          F2Pool 主账户名
     * @param miningUserName    新增的子账户名称
     */
    @ApiOperation(value = "添加子帐户")
    @PostMapping("/v2/mining_user/add")
    public AjaxResult addMiningUser(@RequestParam("username") String username,
                                    @RequestParam("mining_user_name") String miningUserName) {
        JSONObject data = f2PoolService.addMiningUser(username, miningUserName);
        return AjaxResult.success(data);
    }

    /**
     * 获取主账户下的所有子账户列表
     *
     * @param username F2Pool 主账户名
     */
    @ApiOperation(value = "获取子帐户列表")
    @PostMapping("/v2/mining_user/list")
    public AjaxResult listMiningUsers(@RequestParam("username") String username) {
        JSONObject data = f2PoolService.listMiningUsers(username);
        return AjaxResult.success(data);
    }

    //====================资产情况====================

    /**
     * 获取用户资产余额信息
     *
     * @param username                      F2Pool 主账户名
     * @param currency                      币种（如 BTC、ETH）
     * @param miningUserName                子账户名称
     * @param address                       钱包地址
     * @param calculateEstimatedIncome      是否计算预估收益
     * @param historicalTotalIncomeOutcome  是否返回历史总收益
     */
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

    /**
     * 查询收支流水账单
     *
     * @param username       F2Pool 主账户名
     * @param currency       币种
     * @param miningUserName 子账户名称
     * @param address        钱包地址
     * @param type           流水类型（income / outcome）
     * @param startTime      开始时间（时间戳）
     * @param endTime        结束时间（时间戳）
     */
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

    /**
     * 查询单个账户的算力数据
     *
     * @param username       F2Pool 主账户名
     * @param miningUserName 子账户名称
     * @param address        钱包地址
     * @param currency       币种
     */
    @ApiOperation(value = "算力数据")
    @PostMapping("/v2/hash_rate/info")
    public AjaxResult getHashRate(@RequestParam("username") String username,
                                  @RequestParam("mining_user_name") String miningUserName,
                                  @RequestParam("address") String address,
                                  @RequestParam("currency") String currency){
        JSONObject data = f2PoolService.getHashRate(username, miningUserName, address, currency);
        return AjaxResult.success(data);
    }

    /**
     * 同时查询多个子账户的算力数据
     *
     * @param username F2Pool 主账户名
     * @param reqs     多账户请求参数集合（子账户 + 地址 + 币种）
     */
    @ApiOperation(value = "同时查询多帐户的算力数据")
    @PostMapping("/v2/hash_rate/info_list")
    public AjaxResult getHashRateList(@RequestParam("username") String username,
                                      @RequestBody List<Map<String, Object>> reqs){
        JSONObject data = f2PoolService.getHashRateList(username, reqs);
        return AjaxResult.success(data);
    }

    /**
     * 获取矿工（Worker）列表
     *
     * @param username       F2Pool 主账户名
     * @param miningUserName 子账户名称
     * @param address        钱包地址
     * @param currency       币种
     */
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