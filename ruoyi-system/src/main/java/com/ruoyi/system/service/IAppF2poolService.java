package com.ruoyi.system.service;

import com.ruoyi.system.domain.vo.F2poolOverviewVo;
import com.ruoyi.system.domain.vo.F2poolMinersVo;
import com.ruoyi.system.domain.vo.AppUserEarningsAggregateVo;

/**
 * APP F2Pool 聚合服务接口
 *
 * 负责对接 F2Pool 第三方接口并聚合为业务需要的数据结构。
 */
public interface IAppF2poolService {
    /**
     * 获取指定用户的 F2Pool 概览信息
     *
     * @param userId APP 用户 ID
     * @return 概览数据（昨日收益、今日收益、总收益、子帐户数量及用户信息）
     */
    F2poolOverviewVo getOverview(Long userId);

    /**
     * 获取当前用户的矿机聚合信息（第三方 F2Pool）
     *
     * 聚合内容：
     * - totalYesterdayIncome：所有子帐户昨日收益之和
     * - totalHashRate：所有子帐户实时算力之和
     * - items：列表项（mining_user_name 与该子帐户 total_income）
     *
     * @param userId APP 用户 ID
     * @return F2Pool 矿机聚合返回体
     */
    F2poolMinersVo getMiners(Long userId);

    /**
     * 获取 APP 收益信息（第三方 F2Pool + 每日汇总表）
     *
     * total：聚合 F2Pool 的昨日收益、今日预估、累计总收益、余额的总和
     * daily_list：读取 app_user_mining_daily_summary 返回每日记录
     */
    AppUserEarningsAggregateVo getEarnings(Long userId);
}
