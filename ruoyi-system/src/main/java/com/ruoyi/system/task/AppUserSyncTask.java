package com.ruoyi.system.task;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.AppUserMiner;
import com.ruoyi.system.domain.vo.F2poolMinerItemVo;
import com.ruoyi.system.domain.vo.F2poolMinersVo;
import com.ruoyi.system.domain.vo.F2poolOverviewVo;
import com.ruoyi.system.service.IAppF2poolService;
import com.ruoyi.system.service.IAppUserMinerService;
import com.ruoyi.system.service.IAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.http.HttpUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.math.BigDecimal;

/**
 * App 用户数据同步定时任务
 *
 * <p>
 * 该任务用于<strong>周期性同步 F2Pool 数据到本地数据库</strong>，主要包含：
 * <ul>
 *   <li>同步用户层面的统计数据（矿机数、总收益、今日/昨日收益）</li>
 *   <li>同步已存在矿机的收益数据（不负责创建矿机）</li>
 * </ul>
 * </p>
 * 
 * @author Jamie
 */
@Component("appUserSyncTask")
public class AppUserSyncTask {

    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private IAppF2poolService appF2poolService;

    @Autowired
    private IAppUserMinerService appUserMinerService;

    /**
     * 同步所有 App 用户的 F2Pool 数据
     *
     * <p>
     * 执行流程：
     * <ol>
     *   <li>查询所有 App 用户</li>
     *   <li>过滤未配置 F2Pool Token 的用户</li>
     *   <li>逐个用户同步概览数据和矿机收益</li>
     * </ol>
     * </p>
     *
     * <p>
     * 通常由定时调度器（如每日 / 每小时）触发执行
     * </p>
     */
    public void syncAll() {
        System.out.println("开始执行App用户数据同步任务...");
        
        // 1. 查询所有App用户
        List<AppUser> userList = appUserService.selectList(new AppUser());
        if (userList == null || userList.isEmpty()) {
            return;
        }

        for (AppUser user : userList) {
            try {
                // 跳过未配置 F2Pool Token 的用户
                if (StringUtils.isEmpty(user.getF2poolToken())) {
                    continue;
                }

                System.out.println("正在同步用户: " + user.getName() + " (ID: " + user.getId() + ")");
                syncUser(user);

            } catch (Exception e) {
                // 单个用户失败不影响其他用户同步
                System.err.println("同步用户 " + user.getId() + " 失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("App用户数据同步任务执行结束");
    }

    /**
     * 同步单个用户的 F2Pool 数据
     *
     * <p>
     * 包含两部分：
     * <ul>
     *   <li>用户维度的概览统计数据</li>
     *   <li>该用户下已存在矿机的收益数据</li>
     * </ul>
     * </p>
     *
     * @param user App 用户实体
     */
    private void syncUser(AppUser user) {
        Long userId = user.getId();

        // 2. 获取 F2Pool 概览数据 (包含矿机总数、收益统计)
        F2poolOverviewVo overview = appF2poolService.getOverview(userId);
        if (overview != null) {
            BigDecimal btcYesterday = overview.getTotalYesterdayIncome();
            BigDecimal btcTotal = overview.getTotalIncome();
            BigDecimal btcToday = overview.getTotalTodayIncome();

            BigDecimal cnyYesterday = convertBtcToCny(btcYesterday);
            BigDecimal cnyTotal = convertBtcToCny(btcTotal);
            BigDecimal cnyToday = convertBtcToCny(btcToday);

            AppUser userUpdate = new AppUser();
            userUpdate.setId(userId);

            // 只有当 F2Pool 返回有效数据时才更新
            if (overview.getMinerCount() != null) {
                userUpdate.setMinerCount(overview.getMinerCount());
            }
            if (cnyTotal != null) {
                userUpdate.setTotalIncome(cnyTotal);
            }
            if (cnyYesterday != null) {
                userUpdate.setYesterdayIncome(cnyYesterday);
            }
            if (cnyToday != null) {
                userUpdate.setTodayIncome(cnyToday);
            }

            appUserService.update(userUpdate);
        }

        // 3. 获取 F2Pool 矿机列表详情 (用于同步 app_user_miner 表)
        F2poolMinersVo minersVo = appF2poolService.getMiners(userId);
        if (minersVo != null && minersVo.getItems() != null) {
            for (F2poolMinerItemVo item : minersVo.getItems()) {
                syncMiner(userId, item);
            }
        }
    }

    /**
     * 同步单台矿机的收益数据
     *
     * <p>
     * 说明：
     * <ul>
     *   <li>仅更新数据库中<strong>已存在</strong>的矿机</li>
     *   <li>不会在此方法中新增矿机记录</li>
     * </ul>
     * </p>
     *
     * @param userId 用户ID
     * @param item   F2Pool 返回的矿机数据项
     */
    private void syncMiner(Long userId, F2poolMinerItemVo item) {
        String miningUserName = item.getMiningUserName();
        if (StringUtils.isEmpty(miningUserName)) {
            return;
        }

        // 根据用户ID + 矿机名定位本地矿机
        AppUserMiner query = new AppUserMiner();
        query.setUserId(userId);
        query.setMiningUserName(miningUserName);
        List<AppUserMiner> existList = appUserMinerService.selectList(query);

        // 仅当矿机已存在时才执行更新
        if (existList != null && !existList.isEmpty()) {
            AppUserMiner existMiner = existList.get(0);
            AppUserMiner updateMiner = new AppUserMiner();
            updateMiner.setId(existMiner.getId());
            updateMiner.setTotalIncome(convertBtcToCny(item.getTotalIncome()));
            appUserMinerService.update(updateMiner);
        }
    }

    private BigDecimal convertBtcToCny(BigDecimal btcAmount) {
        if (btcAmount == null) return BigDecimal.ZERO;
        BigDecimal btcToUsdt = fetchRate("https://www.exchange-rates.org/zh/api/v2/rates/lookup?isoTo=USDT&isoFrom=BTC&amount=1&pageCode=ConverterForPair");
        BigDecimal usdtToCny = fetchRate("https://www.exchange-rates.org/zh/api/v2/rates/lookup?isoTo=CNY&isoFrom=USDT&amount=1&pageCode=ConverterForPair");
        if (btcToUsdt == null || usdtToCny == null) return BigDecimal.ZERO;
        return btcAmount.multiply(btcToUsdt).multiply(usdtToCny);
    }

    private BigDecimal fetchRate(String url) {
        try {
            String rsp = HttpUtils.sendGet(url);
            if (rsp == null || rsp.isEmpty()) return null;
            JSONObject obj = JSON.parseObject(rsp);
            return obj.getBigDecimal("Rate");
        } catch (Exception e) {
            return null;
        }
    }
}
