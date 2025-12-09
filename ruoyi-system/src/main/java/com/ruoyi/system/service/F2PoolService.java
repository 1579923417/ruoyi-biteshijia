package com.ruoyi.system.service;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.Map;

public interface F2PoolService {
    JSONObject getMiningUser(String username, String miningUserName);
    JSONObject addMiningUser(String username, String miningUserName);
    JSONObject listMiningUsers(String username);
    JSONObject updateWalletAddress(String username, List<Map<String, Object>> params);
    JSONObject addReadOnlyPage(String username, String miningUserName, String pageName, String permissions);
    JSONObject deleteReadOnlyPage(String username, String miningUserName, String key);
    JSONObject withdrawBalance(String username, String miningUserName, String currency);
    JSONObject queryWithdrawableBalance(String username, String currency, String miningUserName);
    JSONObject withdrawByAmount(String username, String currency, String miningUserName, Double value);
    JSONObject pausePayment(String username, String currency, String miningUserName);
    JSONObject resumePayment(String username, String currency, String miningUserName);
    JSONObject updateThreshold(String username, String miningUserName, String currency, String threshold);
    JSONObject updateThresholdValue(String username, String miningUserName, String currency, String thresholdValue);
    JSONObject getUserAssets(String username, String currency, String miningUserName, String address, Boolean calculateEstimatedIncome, Boolean historicalTotalIncomeOutcome);
    JSONObject listTransactionHistory(String username, String currency, String miningUserName, String address, String type, Integer startTime, Integer endTime);
    JSONObject switchSettlementMode(String username, String currency, String mode, String miningUserName, Integer activatedAt);
    JSONObject getHashRate(String username, String miningUserName, String address, String currency);
    JSONObject getHashRateList(String username, List<Map<String, Object>> reqs);
    JSONObject getHashRateHistory(String username, String miningUserName, String address, String currency, Integer interval, Integer duration);
    JSONObject listWorkers(String username, String miningUserName, String address, String currency);
    JSONObject getWorkerHashRateHistory(String username, String miningUserName, String address, String currency, String workerName, Integer interval, Integer duration);
    JSONObject getHashRateDistributionInfo(String username, String currency, String distributor, String recipient, Integer startTime, Integer endTime, Double hashRate);
    JSONObject getHashRateDistributionOrders(String username, String currency, String distributor);
    JSONObject getHashRateDistributionSettlements(String username, String currency, String distributor);
    JSONObject distributeHashRate(String username, String currency, String distributor, String recipient, Integer startTime, Integer endTime, Double hashRate);
    JSONObject terminateHashRateDistribution(String username, Integer orderId);
    JSONObject getRevenueDistributionInfo(String username, String currency, String distributor, String recipient);
    JSONObject distributeRevenue(String username, String currency, String distributor, String recipient, String description, Double proportion);
    JSONObject deleteRevenueDistribution(String username, Integer id, String distributor);
    JSONObject getPoolBlocksPaging(String username, Integer page, Integer pagesize, String currency);
    JSONObject getPoolBlocksByDateRange(String username, String currency, Integer startTime, Integer endTime);
    JSONObject getUserPPLNSRevenue(String username, String currency, String miningUserName, Integer miningReward, Integer startTime, Integer endTime);
}
