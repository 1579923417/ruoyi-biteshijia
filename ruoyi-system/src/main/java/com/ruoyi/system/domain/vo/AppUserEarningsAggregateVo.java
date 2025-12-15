package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * APP 收益信息聚合返回
 *
 * - total：F2Pool 收益汇总（昨日/今日预估/累计/余额）
 * - daily_list：每日收益记录列表（从 app_user_mining_daily_summary 表）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserEarningsAggregateVo {
    private F2poolEarningsTotalVo total;
    private List<AppUserDailySummaryItemVo> dailyList;

    public F2poolEarningsTotalVo getTotal() { return total; }
    public void setTotal(F2poolEarningsTotalVo total) { this.total = total; }
    public List<AppUserDailySummaryItemVo> getDailyList() { return dailyList; }
    public void setDailyList(List<AppUserDailySummaryItemVo> dailyList) { this.dailyList = dailyList; }
}

