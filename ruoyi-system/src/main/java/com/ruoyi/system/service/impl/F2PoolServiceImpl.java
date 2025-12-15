package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.F2poolAccount;
import com.ruoyi.system.domain.WebsiteConfig;
import com.ruoyi.system.factory.ProxyFactory;
import com.ruoyi.system.service.F2PoolService;
import com.ruoyi.system.service.F2poolAccountService;
import com.ruoyi.system.service.IWebsiteConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理--f2pool用户管理 业务层处理
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@Service
public class F2PoolServiceImpl implements F2PoolService {
    @Autowired
    private F2poolAccountService f2poolAccountService;

    @Autowired
    private ProxyFactory proxyFactory;

    private F2poolAccount resolveAccount(String username){
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("F2Pool 用户名不能为空");
        }
        F2poolAccount acc = f2poolAccountService.getByUsername(username);
        if (acc == null) {
            throw new RuntimeException("未配置该用户名对应的 F2Pool 账号");
        }
        if (acc.getStatus() != null && acc.getStatus() == 0) {
            throw new RuntimeException("该 F2Pool 账号已禁用");
        }
        return acc;
    }

    private String getBaseFor(String username){
        F2poolAccount acc = resolveAccount(username);
        String base = acc.getBaseUrl();
        if (base != null && !base.isEmpty()) return base;
        return "https://api.f2pool.com";
    }

    private String getTokenFor(String username){
        F2poolAccount acc = resolveAccount(username);
        String token = acc.getApiToken();
        if (token != null && !token.isEmpty()) return token;
        throw new RuntimeException("未配置该用户名对应的 F2Pool API Token");
    }

    private String postJson(String url, Map<String, Object> body, Map<String, String> headers) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn =
                    (HttpURLConnection) u.openConnection(proxyFactory.buildHttpProxy());

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            if (headers != null) {
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    conn.setRequestProperty(e.getKey(), e.getValue());
                }
            }

            String json = JSON.toJSONString(body != null ? body : new HashMap<>());
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            code >= 200 && code < 300
                                    ? conn.getInputStream()
                                    : conn.getErrorStream(),
                            StandardCharsets.UTF_8))) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public JSONObject getMiningUser(String username, String miningUserName){
        String url = getBaseFor(username) + "/v2/mining_user/get";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject addMiningUser(String username, String miningUserName){
        String url = getBaseFor(username) + "/v2/mining_user/add";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject listMiningUsers(String username){
        String url = getBaseFor(username) + "/v2/mining_user/list";
        Map<String, Object> params = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject updateWalletAddress(String username, List<Map<String, Object>> params){
        String url = getBaseFor(username) + "/v2/mining_user/wallet/update";
        Map<String, Object> body = new HashMap<>();
        body.put("params", params);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, body, header);
        return JSON.parseObject(resp);
    }
    public JSONObject addReadOnlyPage(String username, String miningUserName, String pageName, String permissions){
        String url = getBaseFor(username) + "/v2/mining_user/read_only_page/add";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("page_name", pageName);
        params.put("permissions", permissions);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject deleteReadOnlyPage(String username, String miningUserName, String key){
        String url = getBaseFor(username) + "/v2/mining_user/read_only_page/delete";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("key", key);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject withdrawBalance(String username, String miningUserName, String currency){
        String url = getBaseFor(username) + "/v2/mining_user/balance/withdraw";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("currency", currency);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject queryWithdrawableBalance(String username, String currency, String miningUserName){
        String url = getBaseFor(username) + "/v2/mining_user/balance/withdraw_with_value/query";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_name", miningUserName);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject withdrawByAmount(String username, String currency, String miningUserName, Double value){
        String url = getBaseFor(username) + "/v2/mining_user/balance/withdraw_with_value";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_name", miningUserName);
        params.put("value", value);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject pausePayment(String username, String currency, String miningUserName){
        String url = getBaseFor(username) + "/v2/mining_user/payment/pause";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_names", miningUserName);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject resumePayment(String username, String currency, String miningUserName){
        String url = getBaseFor(username) + "/v2/mining_user/payment/resume";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_names", miningUserName);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject updateThreshold(String username, String miningUserName, String currency, String threshold){
        String url = getBaseFor(username) + "/v2/mining_user/threshold/update";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("currency", currency);
        params.put("threshold", threshold);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject updateThresholdValue(String username, String miningUserName, String currency, String thresholdValue){
        String url = getBaseFor(username) + "/v2/mining_user/threshold/update_value";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("currency", currency);
        params.put("threshold_value", thresholdValue);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getUserAssets(String username, String currency, String miningUserName, String address, Boolean calculateEstimatedIncome, Boolean historicalTotalIncomeOutcome){
        String url = getBaseFor(username) + "/v2/assets/balance";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("calculate_estimated_income", calculateEstimatedIncome);
        params.put("historical_total_income_outcome", historicalTotalIncomeOutcome);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject listTransactionHistory(String username, String currency, String miningUserName, String address, String type, Integer startTime, Integer endTime){
        String url = getBaseFor(username) + "/v2/assets/transactions/list";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("type", type);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject switchSettlementMode(String username, String currency, String mode, String miningUserName, Integer activatedAt){
        String url = getBaseFor(username) + "/v2/assets/settle_mode/switch";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mode", mode);
        params.put("mining_user_name", miningUserName);
        params.put("activated_at", activatedAt);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getHashRate(String username, String miningUserName, String address, String currency){
        String url = getBaseFor(username) + "/v2/hash_rate/info";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("currency", currency);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getHashRateList(String username, List<Map<String, Object>> reqs){
        String url = getBaseFor(username) + "/v2/hash_rate/info_list";
        Map<String, Object> body = new HashMap<>();
        body.put("reqs", reqs);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, body, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getHashRateHistory(String username, String miningUserName, String address, String currency, Integer interval, Integer duration){
        String url = getBaseFor(username) + "/v2/hash_rate/history";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("currency", currency);
        params.put("interval", interval);
        params.put("duration", duration);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject listWorkers(String username, String miningUserName, String address, String currency){
        String url = getBaseFor(username) + "/v2/hash_rate/worker/list";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("currency", currency);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getWorkerHashRateHistory(String username, String miningUserName, String address, String currency, String workerName, Integer interval, Integer duration){
        String url = getBaseFor(username) + "/v2/hash_rate/worker/history";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("currency", currency);
        params.put("worker_name", workerName);
        params.put("interval", interval);
        params.put("duration", duration);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getHashRateDistributionInfo(String username, String currency, String distributor, String recipient, Integer startTime, Integer endTime, Double hashRate){
        String url = getBaseFor(username) + "/v2/hash_rate/distribution/info";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("distributor", distributor);
        params.put("recipient", recipient);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("hash_rate", hashRate);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getHashRateDistributionOrders(String username, String currency, String distributor){
        String url = getBaseFor(username) + "/v2/hash_rate/distribution/orders";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("distributor", distributor);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getHashRateDistributionSettlements(String username, String currency, String distributor){
        String url = getBaseFor(username) + "/v2/hash_rate/distribution/settlements";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("distributor", distributor);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject distributeHashRate(String username, String currency, String distributor, String recipient, Integer startTime, Integer endTime, Double hashRate){
        String url = getBaseFor(username) + "/v2/hash_rate/distribute";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("distributor", distributor);
        params.put("recipient", recipient);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("hash_rate", hashRate);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject terminateHashRateDistribution(String username, Integer orderId){
        String url = getBaseFor(username) + "/v2/hash_rate/distribution/terminate";
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", orderId);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject getRevenueDistributionInfo(String username, String currency, String distributor, String recipient){
        String url = getBaseFor(username) + "/v2/revenue/distribution/info";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("distributor", distributor);
        params.put("recipient", recipient);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject distributeRevenue(String username, String currency, String distributor, String recipient, String description, Double proportion){
        String url = getBaseFor(username) + "/v2/revenue/distribution/add";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("distributor", distributor);
        params.put("recipient", recipient);
        params.put("description", description);
        params.put("proportion", proportion);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
    public JSONObject deleteRevenueDistribution(String username, Integer id, String distributor){
        String url = getBaseFor(username) + "/v2/revenue/distribution/delete";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("distributor", distributor);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }

    public JSONObject getPoolBlocksPaging(String username, Integer page, Integer pagesize, String currency){
        String url = getBaseFor(username) + "/v2/blocks/paging";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("pagesize", pagesize);
        params.put("currency", currency);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }

    public JSONObject getPoolBlocksByDateRange(String username, String currency, Integer startTime, Integer endTime){
        String url = getBaseFor(username) + "/v2/blocks/date_range";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }

    public JSONObject getUserPPLNSRevenue(String username, String currency, String miningUserName, Integer miningReward, Integer startTime, Integer endTime){
        String url = getBaseFor(username) + "/v2/blocks/user";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_name", miningUserName);
        params.put("mining_reward", miningReward);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        Map<String, String> header = new HashMap<>();
        header.put("F2P-API-SECRET", getTokenFor(username));
        String resp = postJson(url, params, header);
        return JSON.parseObject(resp);
    }
}

