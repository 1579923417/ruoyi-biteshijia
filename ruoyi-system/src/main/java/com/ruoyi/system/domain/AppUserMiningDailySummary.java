package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户矿机每日收益汇总实体
 *
 * 对应数据表：app_user_mining_daily_summary
 * 字段说明：
 * - userId：所属用户ID
 * - statDate：统计日期（UTC+8 自然日）
 * - totalIncome：所有子账户累计总收益
 * - yesterdayIncome：昨日收益汇总
 * - balance：当前余额汇总（字符串表示）
 * - updateTime：更新时间
 */
public class AppUserMiningDailySummary {
    private Long id;
    private Long userId;
    private Date statDate;
    private BigDecimal income;
    private BigDecimal h24HashRate;
    private String amount;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Date getStatDate() { return statDate; }
    public void setStatDate(Date statDate) { this.statDate = statDate; }
    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
    public BigDecimal getH24HashRate() { return h24HashRate; }
    public void setH24HashRate(BigDecimal h24HashRate) { this.h24HashRate = h24HashRate; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
