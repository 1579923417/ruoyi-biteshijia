package com.ruoyi.web.controller.f2pool;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.F2PoolService;
import com.ruoyi.common.annotation.Anonymous;
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

    @ApiOperation(value = "变更钱包地址")
    @PostMapping("/v2/mining_user/wallet/update")
    public AjaxResult updateWalletAddress(@RequestParam("username") String username,
                                          @RequestBody List<Map<String, Object>> params){
        JSONObject data = f2PoolService.updateWalletAddress(username, params);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "新增观察者链接")
    @PostMapping("/v2/mining_user/read_only_page/add")
    public AjaxResult addReadOnlyPage(@RequestParam("username") String username,
                                      @RequestParam("mining_user_name") String miningUserName,
                                      @RequestParam("page_name") String pageName,
                                      @RequestParam("permissions") String permissions){
        JSONObject data = f2PoolService.addReadOnlyPage(username, miningUserName, pageName, permissions);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "删除观察者链接")
    @PostMapping("/v2/mining_user/read_only_page/delete")
    public AjaxResult deleteReadOnlyPage(@RequestParam("username") String username,
                                         @RequestParam("mining_user_name") String miningUserName,
                                         @RequestParam("key") String key){
        JSONObject data = f2PoolService.deleteReadOnlyPage(username, miningUserName, key);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "小额提币")
    @PostMapping("/v2/mining_user/balance/withdraw")
    public AjaxResult withdrawBalance(@RequestParam("username") String username,
                                      @RequestParam("mining_user_name") String miningUserName,
                                      @RequestParam("currency") String currency){
        JSONObject data = f2PoolService.withdrawBalance(username, miningUserName, currency);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "指定金额提币的可用数据")
    @PostMapping("/v2/mining_user/balance/withdraw_with_value/query")
    public AjaxResult queryWithdrawableBalance(@RequestParam("username") String username,
                                               @RequestParam("currency") String currency,
                                               @RequestParam("mining_user_name") String miningUserName){
        JSONObject data = f2PoolService.queryWithdrawableBalance(username, currency, miningUserName);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "指定金额提币")
    @PostMapping("/v2/mining_user/balance/withdraw_with_value")
    public AjaxResult withdrawByAmount(@RequestParam("username") String username,
                                       @RequestParam("currency") String currency,
                                       @RequestParam("mining_user_name") String miningUserName,
                                       @RequestParam("value") Double value){
        JSONObject data = f2PoolService.withdrawByAmount(username, currency, miningUserName, value);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "暂停支付")
    @PostMapping("/v2/mining_user/payment/pause")
    public AjaxResult pausePayment(@RequestParam("username") String username,
                                   @RequestParam("currency") String currency,
                                   @RequestParam("mining_user_names") String miningUserName){
        JSONObject data = f2PoolService.pausePayment(username, currency, miningUserName);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "恢复支付")
    @PostMapping("/v2/mining_user/payment/resume")
    public AjaxResult resumePayment(@RequestParam("username") String username,
                                    @RequestParam("currency") String currency,
                                    @RequestParam("mining_user_names") String miningUserName){
        JSONObject data = f2PoolService.resumePayment(username, currency, miningUserName);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "更新起付额")
    @PostMapping("/v2/mining_user/threshold/update")
    public AjaxResult updateThreshold(@RequestParam("username") String username,
                                      @RequestParam("mining_user_name") String miningUserName,
                                      @RequestParam("currency") String currency,
                                      @RequestParam("threshold") String threshold){
        JSONObject data = f2PoolService.updateThreshold(username, miningUserName, currency, threshold);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "更新起付额值")
    @PostMapping("/v2/mining_user/threshold/update_value")
    public AjaxResult updateThresholdValue(@RequestParam("username") String username,
                                           @RequestParam("mining_user_name") String miningUserName,
                                           @RequestParam("currency") String currency,
                                           @RequestParam("threshold_value") String thresholdValue){
        JSONObject data = f2PoolService.updateThresholdValue(username, miningUserName, currency, thresholdValue);
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

    @ApiOperation(value = "更新结算方式")
    @PostMapping("/v2/assets/settle_mode/switch")
    public AjaxResult switchSettlementMode(@RequestParam("username") String username,
                                           @RequestParam("currency") String currency,
                                           @RequestParam("mode") String mode,
                                           @RequestParam("mining_user_name") String miningUserName,
                                           @RequestParam("activated_at") Integer activatedAt){
        JSONObject data = f2PoolService.switchSettlementMode(username, currency, mode, miningUserName, activatedAt);
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

    @ApiOperation(value = "算力历史数据（曲线数据）")
    @PostMapping("/v2/hash_rate/history")
    public AjaxResult getHashRateHistory(@RequestParam("username") String username,
                                         @RequestParam("mining_user_name") String miningUserName,
                                         @RequestParam("address") String address,
                                         @RequestParam("currency") String currency,
                                         @RequestParam("interval") Integer interval,
                                         @RequestParam("duration") Integer duration){
        JSONObject data = f2PoolService.getHashRateHistory(username, miningUserName, address, currency, interval, duration);
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

    @ApiOperation(value = "矿工算力历史数据（曲线数据）")
    @PostMapping("/v2/hash_rate/worker/history")
    public AjaxResult getWorkerHashRateHistory(@RequestParam("username") String username,
                                               @RequestParam("mining_user_name") String miningUserName,
                                               @RequestParam("address") String address,
                                               @RequestParam("currency") String currency,
                                               @RequestParam("worker_name") String workerName,
                                               @RequestParam("interval") Integer interval,
                                               @RequestParam("duration") Integer duration){
        JSONObject data = f2PoolService.getWorkerHashRateHistory(username, miningUserName, address, currency, workerName, interval, duration);
        return AjaxResult.success(data);
    }

    //====================算力分配====================
    @ApiOperation(value = "算力分配信息查询")
    @PostMapping("/v2/hash_rate/distribution/info")
    public AjaxResult getHashRateDistributionInfo(@RequestParam("username") String username,
                                                  @RequestParam("currency") String currency,
                                                  @RequestParam("distributor") String distributor,
                                                  @RequestParam("recipient") String recipient,
                                                  @RequestParam("start_time") Integer startTime,
                                                  @RequestParam("end_time") Integer endTime,
                                                  @RequestParam("hash_rate") Double hashRate){
        JSONObject data = f2PoolService.getHashRateDistributionInfo(username, currency, distributor, recipient, startTime, endTime, hashRate);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "算力分配订单查询")
    @PostMapping("/v2/hash_rate/distribution/orders")
    public AjaxResult getHashRateDistributionOrders(@RequestParam("username") String username,
                                                    @RequestParam("currency") String currency,
                                                    @RequestParam("distributor") String distributor){
        JSONObject data = f2PoolService.getHashRateDistributionOrders(username, currency, distributor);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "算力分配结算查询")
    @PostMapping("/v2/hash_rate/distribution/settlements")
    public AjaxResult getHashRateDistributionSettlements(@RequestParam("username") String username,
                                                         @RequestParam("currency") String currency,
                                                         @RequestParam("distributor") String distributor){
        JSONObject data = f2PoolService.getHashRateDistributionSettlements(username, currency, distributor);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "分配算力给挖矿帐户")
    @PostMapping("/v2/hash_rate/distribute")
    public AjaxResult distributeHashRate(@RequestParam("username") String username,
                                         @RequestParam("currency") String currency,
                                         @RequestParam("distributor") String distributor,
                                         @RequestParam("recipient") String recipient,
                                         @RequestParam("start_time") Integer startTime,
                                         @RequestParam("end_time") Integer endTime,
                                         @RequestParam("hash_rate") Double hashRate){
        JSONObject data = f2PoolService.distributeHashRate(username, currency, distributor, recipient, startTime, endTime, hashRate);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "终止算力分配订单")
    @PostMapping("/v2/hash_rate/distribution/terminate")
    public AjaxResult terminateHashRateDistribution(@RequestParam("username") String username,
                                                    @RequestParam("order_id") Integer orderId){
        JSONObject data = f2PoolService.terminateHashRateDistribution(username, orderId);
        return AjaxResult.success(data);
    }

    //====================收益分配====================
    @ApiOperation(value = "收益分配信息查询")
    @PostMapping("/v2/revenue/distribution/info")
    public AjaxResult getRevenueDistributionInfo(@RequestParam("username") String username,
                                                 @RequestParam("currency") String currency,
                                                 @RequestParam("distributor") String distributor,
                                                 @RequestParam("recipient") String recipient){
        JSONObject data = f2PoolService.getRevenueDistributionInfo(username, currency, distributor, recipient);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "分配收益给挖矿帐户")
    @PostMapping("/v2/revenue/distribution/add")
    public AjaxResult distributeRevenue(@RequestParam("username") String username,
                                        @RequestParam("currency") String currency,
                                        @RequestParam("distributor") String distributor,
                                        @RequestParam("recipient") String recipient,
                                        @RequestParam("description") String description,
                                        @RequestParam("proportion") Double proportion){
        JSONObject data = f2PoolService.distributeRevenue(username, currency, distributor, recipient, description, proportion);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "取消算力分配记录")
    @PostMapping("/v2/revenue/distribution/delete")
    public AjaxResult deleteRevenueDistribution(@RequestParam("username") String username,
                                                @RequestParam("id") Integer id,
                                                @RequestParam("distributor") String distributor){
        JSONObject data = f2PoolService.deleteRevenueDistribution(username, id, distributor);
        return AjaxResult.success(data);
    }

    //====================矿池信息====================
    @ApiOperation(value = "矿池出块分页")
    @PostMapping("/v2/blocks/paging")
    public AjaxResult getPoolBlocksPaging(@RequestParam("username") String username,
                                          @RequestParam("page") Integer page,
                                          @RequestParam("pagesize") Integer pagesize,
                                          @RequestParam("currency") String currency){
        JSONObject data = f2PoolService.getPoolBlocksPaging(username, page, pagesize, currency);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "时间范围内矿池出块")
    @PostMapping("/v2/blocks/date_range")
    public AjaxResult getPoolBlocksByDateRange(@RequestParam("username") String username,
                                               @RequestParam("currency") String currency,
                                               @RequestParam("start_time") Integer startTime,
                                               @RequestParam("end_time") Integer endTime){
        JSONObject data = f2PoolService.getPoolBlocksByDateRange(username, currency, startTime, endTime);
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "用户pplns的收益详情")
    @PostMapping("/v2/blocks/user")
    public AjaxResult getUserPPLNSRevenue(@RequestParam("username") String username,
                                          @RequestParam("currency") String currency,
                                          @RequestParam("mining_user_name") String miningUserName,
                                          @RequestParam("mining_reward") Integer miningReward,
                                          @RequestParam("start_time") Integer startTime,
                                          @RequestParam("end_time") Integer endTime){
        JSONObject data = f2PoolService.getUserPPLNSRevenue(username, currency, miningUserName, miningReward, startTime, endTime);
        return AjaxResult.success(data);
    }
}

