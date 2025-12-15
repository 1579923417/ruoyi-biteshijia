package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * F2Pool 收益汇总
 *
 * - yesterdayIncome：昨日收益总和
 * - estimatedTodayIncome：今日预估收益总和
 * - totalIncome：累计总收益总和
 * - balance：当前余额总和
 */
public class F2poolEarningsTotalVo {
    private BigDecimal yesterdayIncome;
    private BigDecimal estimatedTodayIncome;
    private BigDecimal totalIncome;
    private BigDecimal balance;

    public BigDecimal getYesterdayIncome() { return yesterdayIncome; }
    public void setYesterdayIncome(BigDecimal yesterdayIncome) { this.yesterdayIncome = yesterdayIncome; }
    public BigDecimal getEstimatedTodayIncome() { return estimatedTodayIncome; }
    public void setEstimatedTodayIncome(BigDecimal estimatedTodayIncome) { this.estimatedTodayIncome = estimatedTodayIncome; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}

