package com.ruoyi.system.service;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.Map;

public interface F2PoolService {
    JSONObject getMiningUser(String username, String miningUserName);
    JSONObject addMiningUser(String token, String miningUserName);
    JSONObject listMiningUsers(String username);
    JSONObject getUserAssets(String username, String currency, String miningUserName, String address, Boolean calculateEstimatedIncome, Boolean historicalTotalIncomeOutcome);
    JSONObject listTransactionHistory(String username, String currency, String miningUserName, String address, String type, Integer startTime, Integer endTime);
    JSONObject getHashRate(String username, String miningUserName, String address, String currency);
    JSONObject getHashRateList(String username, List<Map<String, Object>> reqs);
    JSONObject listWorkers(String username, String miningUserName, String address, String currency);
    int syncMinerData();
}
