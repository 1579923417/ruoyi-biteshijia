package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.f2pool.F2PoolClient;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.vo.F2poolOverviewVo;
import com.ruoyi.system.domain.vo.F2poolMinersVo;
import com.ruoyi.system.domain.vo.F2poolMinerItemVo;
import com.ruoyi.system.factory.ProxyFactory;
import com.ruoyi.system.domain.AppUserMiningDailySummary;
import com.ruoyi.system.service.IAppUserMiningDailySummaryService;
import com.ruoyi.system.domain.vo.AppUserEarningsAggregateVo;
import com.ruoyi.system.domain.vo.F2poolEarningsTotalVo;
import com.ruoyi.system.domain.vo.AppUserDailySummaryItemVo;
import com.ruoyi.system.service.IAppF2poolService;
import com.ruoyi.system.service.IAppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.Proxy;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * APP F2Pool 聚合服务实现
 *
 * 通过 F2PoolClient 调用第三方接口，聚合用户的子帐户数据
 */
@Service
public class AppF2poolServiceImpl implements IAppF2poolService {

    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private ProxyFactory proxyFactory;

    @Autowired
    private IAppUserMiningDailySummaryService dailySummaryService;

    /**
     * 获取当前用户的 F2Pool 概览数据
     *
     * 处理流程：
     * 1. 调用 /v2/mining_user/list 获取子帐户列表并统计 minerCount
     * 2. 遍历每个子帐户的钱包，调用 /v2/assets/balance 累加昨日收益与总收益
     * 3. 计算今日总收益 = 总收益 - 昨日收益
     * 4. 组合用户基本信息与聚合结果并返回
     *
     * 返回字段：
     * - totalYesterdayIncome：昨日收益总和
     * - totalIncome：总收益总和
     * - totalTodayIncome：今日总收益（总收益 - 昨日收益）
     * - minerCount：子帐户数量
     * - name/phone/bankName/bankAccount：用户基本信息
     *
     * @param userId APP 用户 ID
     * @return 概览数据对象
     */
    public F2poolOverviewVo getOverview(Long userId) {
        AppUser user = appUserService.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Proxy proxy = proxyFactory.buildHttpProxy();
        F2PoolClient client = new F2PoolClient(user.getF2poolUrl(), user.getF2poolToken(), proxy);

        JSONObject listResp = client.listMiningUsers();
        JSONArray arr = listResp.getJSONArray("mining_user_list");

        BigDecimal totalYesterday = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;

        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = arr.getJSONObject(i);
                String miningUserName = item.getString("mining_user_name");
                JSONArray wallets = item.getJSONArray("wallets");
                if (wallets == null || wallets.isEmpty()) {
                    continue;
                }
                for (int w = 0; w < wallets.size(); w++) {
                    JSONObject wallet = wallets.getJSONObject(w);
                    String currency = wallet.getString("currency");
                    String address = wallet.getString("address");
                    if (StringUtils.isEmpty(currency) || StringUtils.isEmpty(address)) {
                        continue;
                    }
                    JSONObject balResp = client.getUserAssets(currency, miningUserName, address, true, true);
                    JSONObject balanceInfo = balResp.getJSONObject("balance_info");
                    if (balanceInfo != null) {
                        BigDecimal y = balanceInfo.getBigDecimal("yesterday_income");
                        BigDecimal t = balanceInfo.getBigDecimal("total_income");
                        if (y != null) totalYesterday = totalYesterday.add(y);
                        if (t != null) totalIncome = totalIncome.add(t);
                    }
                }
            }
        }

        F2poolOverviewVo vo = new F2poolOverviewVo();
        vo.setTotalYesterdayIncome(totalYesterday);
        vo.setTotalIncome(totalIncome);
        vo.setTotalTodayIncome(totalIncome.subtract(totalYesterday));
        vo.setMinerCount(arr == null ? 0 : arr.size());
        vo.setName(user.getName());
        vo.setPhone(user.getPhone());
        vo.setBankName(user.getBankName());
        vo.setBankAccount(user.getBankAccount());
        return vo;
    }

    /**
     * 获取当前用户的矿机列表（F2Pool 聚合）
     *
     * 处理流程：
     * 1. 调用 /v2/mining_user/list 获取子帐户列表
     * 2. 遍历每个子帐户的钱包，调用 /v2/assets/balance 累加昨日收益与总收益
     * 3. 使用 /v2/hash_rate/info_list 查询所有子帐户的实时算力并加总
     * 4. 返回 totalYesterdayIncome、totalHashRate 以及 items（mining_user_name、total_income）
     *
     * @param userId APP 用户 ID
     * @return
     */
    public F2poolMinersVo getMiners(Long userId) {
        AppUser user = appUserService.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Proxy proxy = proxyFactory.buildHttpProxy();
        F2PoolClient client = new F2PoolClient(user.getF2poolUrl(), user.getF2poolToken(), proxy);

        JSONObject listResp = client.listMiningUsers();
        JSONArray arr = listResp.getJSONArray("mining_user_list");

        BigDecimal totalYesterday = BigDecimal.ZERO;
        Map<String, BigDecimal> perUserTotalIncome = new LinkedHashMap<>();

        List<Map<String, Object>> reqs = new ArrayList<>();

        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = arr.getJSONObject(i);
                String miningUserName = item.getString("mining_user_name");
                perUserTotalIncome.put(miningUserName, BigDecimal.ZERO);
                Map<String, Object> r = new HashMap<>();
                r.put("name", miningUserName);
                reqs.add(r);

                JSONArray wallets = item.getJSONArray("wallets");
                if (wallets == null || wallets.isEmpty()) {
                    continue;
                }
                for (int w = 0; w < wallets.size(); w++) {
                    JSONObject wallet = wallets.getJSONObject(w);
                    String currency = wallet.getString("currency");
                    String address = wallet.getString("address");
                    if (StringUtils.isEmpty(currency) || StringUtils.isEmpty(address)) {
                        continue;
                    }
                    JSONObject balResp = client.getUserAssets(currency, miningUserName, address, true, true);
                    JSONObject balanceInfo = balResp.getJSONObject("balance_info");
                    if (balanceInfo != null) {
                        BigDecimal y = balanceInfo.getBigDecimal("yesterday_income");
                        BigDecimal t = balanceInfo.getBigDecimal("total_income");
                        if (y != null) totalYesterday = totalYesterday.add(y);
                        if (t != null) perUserTotalIncome.put(miningUserName, perUserTotalIncome.get(miningUserName).add(t));
                    }
                }
            }
        }

        BigDecimal totalHashRate = BigDecimal.ZERO;
        if (!reqs.isEmpty()) {
            JSONObject hrResp = client.getHashRateList(reqs);
            JSONObject data = hrResp.getJSONObject("data");
            JSONArray info = data != null ? data.getJSONArray("info") : hrResp.getJSONArray("info");
            if (info != null) {
                for (int i = 0; i < info.size(); i++) {
                    JSONObject it = info.getJSONObject(i);
                    BigDecimal hr = it.getBigDecimal("hash_rate");
                    if (hr != null) {
                        totalHashRate = totalHashRate.add(hr);
                    }
                }
            }
        }

        List<F2poolMinerItemVo> items = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : perUserTotalIncome.entrySet()) {
            F2poolMinerItemVo item = new F2poolMinerItemVo();
            item.setMiningUserName(e.getKey());
            item.setTotalIncome(e.getValue());
            items.add(item);
        }

        F2poolMinersVo vo = new F2poolMinersVo();
        vo.setTotalYesterdayIncome(totalYesterday);
        vo.setTotalHashRate(totalHashRate);
        vo.setItems(items);
        return vo;
    }

    /**
     * 获取 APP 收益信息（F2Pool 总和 + 每日汇总列表）
     */
    public AppUserEarningsAggregateVo getEarnings(Long userId) {
        AppUser user = appUserService.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Proxy proxy = proxyFactory.buildHttpProxy();
        F2PoolClient client = new F2PoolClient(user.getF2poolUrl(), user.getF2poolToken(), proxy);

        JSONObject listResp = client.listMiningUsers();
        JSONArray arr = listResp.getJSONArray("mining_user_list");

        BigDecimal sumYesterday = BigDecimal.ZERO;
        BigDecimal sumEstimatedToday = BigDecimal.ZERO;
        BigDecimal sumTotalIncome = BigDecimal.ZERO;
        BigDecimal sumBalance = BigDecimal.ZERO;

        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = arr.getJSONObject(i);
                String miningUserName = item.getString("mining_user_name");
                JSONArray wallets = item.getJSONArray("wallets");
                if (wallets == null || wallets.isEmpty()) {
                    continue;
                }
                for (int w = 0; w < wallets.size(); w++) {
                    JSONObject wallet = wallets.getJSONObject(w);
                    String currency = wallet.getString("currency");
                    String address = wallet.getString("address");
                    if (StringUtils.isEmpty(currency) || StringUtils.isEmpty(address)) {
                        continue;
                    }
                    JSONObject balResp = client.getUserAssets(currency, miningUserName, address, true, true);
                    JSONObject bi = balResp.getJSONObject("balance_info");
                    if (bi != null) {
                        BigDecimal y = bi.getBigDecimal("yesterday_income");
                        BigDecimal e = bi.getBigDecimal("estimated_today_income");
                        BigDecimal t = bi.getBigDecimal("total_income");
                        BigDecimal b = bi.getBigDecimal("balance");
                        if (y != null) sumYesterday = sumYesterday.add(y);
                        if (e != null) sumEstimatedToday = sumEstimatedToday.add(e);
                        if (t != null) sumTotalIncome = sumTotalIncome.add(t);
                        if (b != null) sumBalance = sumBalance.add(b);
                    }
                }
            }
        }

        F2poolEarningsTotalVo total = new F2poolEarningsTotalVo();
        total.setYesterdayIncome(sumYesterday);
        total.setEstimatedTodayIncome(sumEstimatedToday);
        total.setTotalIncome(sumTotalIncome);
        total.setBalance(sumBalance);

        AppUserMiningDailySummary query = new AppUserMiningDailySummary();
        query.setUserId(userId);
        List<AppUserMiningDailySummary> rows = dailySummaryService.selectList(query);

        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<AppUserDailySummaryItemVo> dailyList = new ArrayList<>();
        if (rows != null) {
            for (AppUserMiningDailySummary s : rows) {
                AppUserDailySummaryItemVo d = new AppUserDailySummaryItemVo();
                String dateStr = s.getStatDate() == null ? null : s.getStatDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(df);
                d.setStatDate(dateStr);
                d.setIncome(s.getIncome());
                d.setAmount(s.getAmount());
                d.setH24HashRate(s.getH24HashRate());
                dailyList.add(d);
            }
        }

        AppUserEarningsAggregateVo vo = new AppUserEarningsAggregateVo();
        vo.setTotal(total);
        vo.setDailyList(dailyList);
        return vo;
    }
}
