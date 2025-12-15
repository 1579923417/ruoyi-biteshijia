package com.ruoyi.common.utils.f2pool;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * F2Pool 客户端工具
 *
 * 封装了与 F2Pool 第三方接口的 HTTP 交互，调用方仅需提供基础配置即可完成请求：
 * - baseUrl：接口基础地址，允许为空；为空时默认使用 {@code https://api.f2pool.com}
 * - token：F2Pool 的 API 密钥，必填，通过请求头 {@code F2P-API-SECRET} 传递
 * - proxy：可选的 HTTP 代理，默认为 {@link java.net.Proxy#NO_PROXY}
 *
 * 使用方式：
 * 1. 构造客户端：new F2PoolClient(baseUrl, token) 或 new F2PoolClient(baseUrl, token, proxy)
 * 2. 调用对应方法获取数据，返回值统一为 {@link com.alibaba.fastjson2.JSONObject}
 *
 * 说明：
 * - 本工具类不负责持久化与业务规则判断；仅专注于第三方调用
 * - 如需统一代理策略，可在业务层构建 {@link java.net.Proxy} 并传入构造方法
 */
public class F2PoolClient {
    private final String baseUrl;
    private final String token;
    private final Proxy proxy;

    /**
     * 使用默认无代理构造客户端
     *
     * @param baseUrl F2Pool 接口基础地址，可为空
     * @param token   F2Pool API 密钥，必填
     * @throws RuntimeException 当 token 未配置或为空时抛出
     */
    public F2PoolClient(String baseUrl, String token) {
        this(baseUrl, token, Proxy.NO_PROXY);
    }

    /**
     * 使用指定代理构造客户端
     *
     * @param baseUrl F2Pool 接口基础地址，可为空
     * @param token   F2Pool API 密钥，必填
     * @param proxy   HTTP 代理，不可用时传入 {@link java.net.Proxy#NO_PROXY}
     * @throws RuntimeException 当 token 未配置或为空时抛出
     */
    public F2PoolClient(String baseUrl, String token, Proxy proxy) {
        this.baseUrl = (baseUrl != null && !baseUrl.isEmpty()) ? baseUrl : "https://api.f2pool.com";
        this.token = token;
        this.proxy = proxy != null ? proxy : Proxy.NO_PROXY;
        if (this.token == null || this.token.isEmpty()) {
            throw new RuntimeException("未配置 F2Pool API Token");
        }
    }

    /**
     * 以 POST 方式发送 JSON 请求并返回字符串响应
     *
     * @param url  完整请求地址
     * @param body 请求体参数，允许为空
     * @return 字符串形式的响应内容
     */
    private String postJson(String url, Map<String, Object> body) {
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection(proxy);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("F2P-API-SECRET", token);

            String json = JSON.toJSONString(body != null ? body : new HashMap<>());
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream(),
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

    /**
     * 获取子帐户列表
     *
     * @return 调用结果
     */
    public JSONObject listMiningUsers() {
        String url = baseUrl + "/v2/mining_user/list";
        return JSON.parseObject(postJson(url, new HashMap<>()));
    }

    /**
     * 获取用户当前资产信息
     *
     * @param currency                      币种
     * @param miningUserName                子帐户名称
     * @param address                       钱包地址
     * @param calculateEstimatedIncome      是否计算预估收入
     * @param historicalTotalIncomeOutcome  是否返回历史总收入支出
     * @return 调用结果
     */
    public JSONObject getUserAssets(String currency, String miningUserName, String address,
                                    Boolean calculateEstimatedIncome, Boolean historicalTotalIncomeOutcome) {
        String url = baseUrl + "/v2/assets/balance";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("calculate_estimated_income", calculateEstimatedIncome);
        params.put("historical_total_income_outcome", historicalTotalIncomeOutcome);
        return JSON.parseObject(postJson(url, params));
    }

    /**
     * 收支流水账单
     *
     * @param currency   币种
     * @param miningUserName 子帐户名称
     * @param address    钱包地址
     * @param type       类型
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 调用结果
     */
    public JSONObject listTransactionHistory(String currency, String miningUserName, String address,
                                             String type, Integer startTime, Integer endTime) {
        String url = baseUrl + "/v2/assets/transactions/list";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("type", type);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        return JSON.parseObject(postJson(url, params));
    }

    /**
     * 同时查询多帐户的算力数据
     *
     * @param reqs 请求列表，每项包含必要的查询字段
     * @return 调用结果
     */
    public JSONObject getHashRateList(List<Map<String, Object>> reqs) {
        String url = baseUrl + "/v2/hash_rate/info_list";
        Map<String, Object> body = new HashMap<>();
        body.put("reqs", reqs);
        return JSON.parseObject(postJson(url, body));
    }

    /**
     * 矿工列表
     *
     * @param miningUserName 子帐户名称
     * @param address        钱包地址
     * @param currency       币种
     * @return 调用结果
     */
    public JSONObject listWorkers(String miningUserName, String address, String currency) {
        String url = baseUrl + "/v2/hash_rate/worker/list";
        Map<String, Object> params = new HashMap<>();
        params.put("mining_user_name", miningUserName);
        params.put("address", address);
        params.put("currency", currency);
        return JSON.parseObject(postJson(url, params));
    }

    /**
     * 矿池出块分页
     *
     * @param page     页码
     * @param pagesize 每页数量
     * @param currency 币种
     * @return 调用结果
     */
    public JSONObject getPoolBlocksPaging(Integer page, Integer pagesize, String currency) {
        String url = baseUrl + "/v2/blocks/paging";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("pagesize", pagesize);
        params.put("currency", currency);
        return JSON.parseObject(postJson(url, params));
    }

    /**
     * 时间范围内矿池出块
     *
     * @param currency  币种
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 调用结果
     */
    public JSONObject getPoolBlocksByDateRange(String currency, Integer startTime, Integer endTime) {
        String url = baseUrl + "/v2/blocks/date_range";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        return JSON.parseObject(postJson(url, params));
    }

    /**
     * 用户 PPLNS 的收益详情
     *
     * @param currency       币种
     * @param miningUserName 子帐户名称
     * @param miningReward   挖矿奖励
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @return 调用结果
     */
    public JSONObject getUserPPLNSRevenue(String currency, String miningUserName, Integer miningReward,
                                          Integer startTime, Integer endTime) {
        String url = baseUrl + "/v2/blocks/user";
        Map<String, Object> params = new HashMap<>();
        params.put("currency", currency);
        params.put("mining_user_name", miningUserName);
        params.put("mining_reward", miningReward);
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        return JSON.parseObject(postJson(url, params));
    }
}
