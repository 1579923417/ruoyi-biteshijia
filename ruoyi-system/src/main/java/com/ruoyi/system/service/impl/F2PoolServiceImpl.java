package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.f2pool.F2PoolClient;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.AppUserMiner;
import com.ruoyi.system.factory.ProxyFactory;
import com.ruoyi.system.service.F2PoolService;
import com.ruoyi.system.service.IAppUserMinerService;
import com.ruoyi.system.service.IAppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

@Service
public class F2PoolServiceImpl implements F2PoolService {
    @Autowired
    private IAppUserService appUserService;
    @Autowired
    private IAppUserMinerService appUserMinerService;
    @Autowired
    private ProxyFactory proxyFactory;

    public JSONObject getMiningUser(String username, String miningUserName) {
        return new JSONObject();
    }

    public JSONObject addMiningUser(String token, String miningUserName) {
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("F2Pool Token不能为空");
        }
        AppUser user = new AppUser();
        user.setF2poolToken(token);
        // Default URL or fetch from somewhere else if needed, but for now assuming default is fine or user object is needed
        // Since we only passed token, we assume default URL.
        Proxy proxy = proxyFactory.buildHttpProxy();
        F2PoolClient client = new F2PoolClient(null, token, proxy);
        return client.addMiningUser(miningUserName);
    }

    public JSONObject listMiningUsers(String username) {
        return new JSONObject();
    }

    public JSONObject getUserAssets(String username, String currency, String miningUserName, String address, Boolean calculateEstimatedIncome, Boolean historicalTotalIncomeOutcome) {
        return new JSONObject();
    }

    public JSONObject listTransactionHistory(String username, String currency, String miningUserName, String address, String type, Integer startTime, Integer endTime) {
        return new JSONObject();
    }

    public JSONObject getHashRate(String username, String miningUserName, String address, String currency) {
        return new JSONObject();
    }

    public JSONObject getHashRateList(String username, List<Map<String, Object>> reqs) {
        return new JSONObject();
    }

    public JSONObject listWorkers(String username, String miningUserName, String address, String currency) {
        return new JSONObject();
    }

    public int syncMinerData() {
        int updated = 0;
        List<AppUser> users = appUserService.selectList(new AppUser());
        if (users == null || users.isEmpty()) {
            return 0;
        }
        for (AppUser user : users) {
            if (StringUtils.isBlank(user.getF2poolToken())) {
                continue;
            }
            Proxy proxy = proxyFactory.buildHttpProxy();
            F2PoolClient client = new F2PoolClient(user.getF2poolUrl(), user.getF2poolToken(), proxy);

            AppUserMiner query = new AppUserMiner();
            query.setUserId(user.getId());
            List<AppUserMiner> existing = appUserMinerService.selectList(query);
            if (existing != null) {
                for (AppUserMiner m : existing) {
                    appUserMinerService.updateStatus(m.getId(), 0);
                }
            }

            JSONObject listResp = client.listMiningUsers();
            JSONArray arr = listResp.getJSONArray("mining_user_list");
            if (arr == null) {
                continue;
            }
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = arr.getJSONObject(i);
                String miningUserName = item.getString("mining_user_name");
                BigDecimal sumYesterday = BigDecimal.ZERO;
                BigDecimal sumTotal = BigDecimal.ZERO;
                JSONArray wallets = item.getJSONArray("wallets");
                if (wallets != null && !wallets.isEmpty()) {
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
                            BigDecimal t = bi.getBigDecimal("total_income");
                            if (y != null) sumYesterday = sumYesterday.add(y);
                            if (t != null) sumTotal = sumTotal.add(t);
                        }
                    }
                }

                // Find existing miner by miningUserName (exact match)
                AppUserMiner entity = null;
                if (existing != null) {
                    for (AppUserMiner m : existing) {
                        // Match by miningUserName (primary) or apiCode (legacy fallback)
                        if (miningUserName.equals(m.getMiningUserName()) ||
                            (m.getMiningUserName() == null && miningUserName.equals(m.getApiCode()))) {
                            entity = m;
                            // Ensure miningUserName is populated for legacy records
                            if (entity.getMiningUserName() == null) {
                                entity.setMiningUserName(miningUserName);
                            }
                            break;
                        }
                    }
                }

                if (entity != null) {
                    entity.setYesterdayIncome(sumYesterday);
                    entity.setTotalIncome(sumTotal);
                    entity.setStatus(1);
                    appUserMinerService.update(entity);
                } else {
                    entity = new AppUserMiner();
                    entity.setUserId(user.getId());
                    entity.setMiningUserName(miningUserName);
                    entity.setYesterdayIncome(sumYesterday);
                    entity.setTotalIncome(sumTotal);
                    entity.setStatus(1);
                    appUserMinerService.insert(entity);
                    // Add to existing list to handle potential duplicates in same sync or just for consistency
                    if (existing != null) {
                        existing.add(entity);
                    }
                }
                updated++;
            }
        }
        return updated;
    }
}

