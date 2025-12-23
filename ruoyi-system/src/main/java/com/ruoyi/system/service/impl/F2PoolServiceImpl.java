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

/**
 * F2Pool 业务实现类
 *
 * 职责说明：
 *  封装与 F2Pool 官方 API 的交互逻辑
 *  同步矿机列表、矿机状态及收益数据
 *  将 F2Pool 数据映射并落库到本地表（app_user / app_user_miner）
 *
 *  本类<strong>不负责用户鉴权</strong>，仅依赖已配置的 F2Pool Token
 *  所有网络请求均支持代理（由 {@link ProxyFactory} 提供）
 *
 * @author Jamie
 */
@Service
public class F2PoolServiceImpl implements F2PoolService {

    @Autowired
    private IAppUserService appUserService;
    @Autowired
    private IAppUserMinerService appUserMinerService;
    @Autowired
    private ProxyFactory proxyFactory;

    /**
     * 获取指定矿机用户信息（预留接口，暂未实现）
     *
     * @param username       平台用户名（预留）
     * @param miningUserName 矿机用户名
     * @return 矿机用户信息 JSON
     */
    @Override
    public JSONObject getMiningUser(String username, String miningUserName) {
        return new JSONObject();
    }

    /**
     * 新增 F2Pool 矿机用户
     *
     * <p>
     * 使用用户配置的 F2Pool Token 调用官方接口创建矿机账户
     * 
     *
     * @param token          F2Pool Token
     * @param miningUserName 矿机用户名
     * @return F2Pool 返回结果
     */
    @Override
    public JSONObject addMiningUser(String token, String miningUserName) {
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("F2Pool Token不能为空");
        }
        AppUser user = new AppUser();
        user.setF2poolToken(token);
        // 构建代理
        Proxy proxy = proxyFactory.buildHttpProxy();
        // 初始化 F2Pool 客户端
        F2PoolClient client = new F2PoolClient(null, token, proxy);
        // 调用 F2Pool 接口创建矿机用户
        return client.addMiningUser(miningUserName);
    }

    /**
     * 获取矿机用户列表
     */
    @Override
    public JSONObject listMiningUsers(String username) {
        return new JSONObject();
    }

    /**
     * 查询用户资产信息
     */
    @Override
    public JSONObject getUserAssets(String username, String currency, String miningUserName, String address, Boolean calculateEstimatedIncome, Boolean historicalTotalIncomeOutcome) {
        return new JSONObject();
    }

    /**
     * 查询交易记录
     */
    @Override
    public JSONObject listTransactionHistory(String username, String currency, String miningUserName, String address, String type, Integer startTime, Integer endTime) {
        return new JSONObject();
    }

    /**
     * 批量获取算力信息
     */
    @Override
    public JSONObject getHashRate(String username, String miningUserName, String address, String currency) {
        return new JSONObject();
    }

    /**
     * 批量获取算力信息
     */
    @Override
    public JSONObject getHashRateList(String username, List<Map<String, Object>> reqs) {
        return new JSONObject();
    }

    /**
     * 获取矿工列表
     */
    @Override
    public JSONObject listWorkers(String username, String miningUserName, String address, String currency) {
        return new JSONObject();
    }

    /**
     * 同步所有 App 用户的矿机数据（核心方法）
     *
     * 执行流程：
     *   查询所有 App 用户
     *   过滤未配置 F2Pool Token 的用户
     *   调用 F2Pool API 拉取矿机列表
     *   汇总每台矿机的昨日收益 & 总收益
     *   同步更新 app_user_miner 表
     *
     * 使用场景：
     *   定时任务（Quartz / Spring Scheduler）
     *   后台手动触发同步
     *
     * @return 成功同步的矿机数量
     */
    @Override
    public int syncMinerData() {
        int result = 0;
        int updated = 0;
        List<AppUser> users = appUserService.selectList(new AppUser());
        if (users != null && !users.isEmpty()) {
            for (AppUser user : users) {
                // 跳过未配置 F2Pool Token 的用户
                if (StringUtils.isBlank(user.getF2poolToken())) {
                    continue;
                }
                // 构建代理并初始化客户端
                Proxy proxy = proxyFactory.buildHttpProxy();
                F2PoolClient client = new F2PoolClient(user.getF2poolUrl(), user.getF2poolToken(), proxy);
                // 2. 查询用户现有矿机，并先统一标记为“离线”
                AppUserMiner query = new AppUserMiner();
                query.setUserId(user.getId());
                List<AppUserMiner> existing = appUserMinerService.selectList(query);
                if (existing != null) {
                    for (AppUserMiner m : existing) {
                        appUserMinerService.updateStatus(m.getId(), 0);
                    }
                }
                // 3. 拉取 F2Pool 矿机列表
                JSONObject listResp = client.listMiningUsers();
                JSONArray arr = listResp.getJSONArray("mining_user_list");
                if (arr == null) {
                    continue;
                }
                // 4. 遍历矿机列表，同步收益数据
                for (int i = 0; i < arr.size(); i++) {
                    // 5. 汇总所有钱包下的收益
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

                    // 6. 匹配本地矿机记录（优先 miningUserName）
                    AppUserMiner entity = null;
                    if (existing != null) {
                        for (AppUserMiner m : existing) {
                            // 匹配规则：
                            // 优先使用 miningUserName 进行精确匹配（当前标准字段）
                            // 若本地记录为历史数据，miningUserName 为空，则使用 apiCode 作为兜底匹配
                            if (miningUserName.equals(m.getMiningUserName()) ||
                                (m.getMiningUserName() == null && miningUserName.equals(m.getApiCode()))) {
                                entity = m;
                                // 对历史数据进行字段修复：
                                // 若本地记录的 miningUserName 为空，则补充写入，避免后续再次走兜底逻辑
                                if (entity.getMiningUserName() == null) {
                                    entity.setMiningUserName(miningUserName);
                                }
                                break;
                            }
                        }
                    }
                    // 7. 更新或新增矿机
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
                        // 将新创建的矿机加入已存在列表，避免同一次同步过程中出现重复处理
                        if (existing != null) {
                            existing.add(entity);
                        }
                    }
                    updated++;
                }
            }
            result = updated;
        }
        return result;
    }
}

