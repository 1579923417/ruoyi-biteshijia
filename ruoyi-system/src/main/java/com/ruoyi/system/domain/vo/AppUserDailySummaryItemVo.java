package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * 每日收益列表项
 *
 * - statDate：统计日期（yyyy-MM-dd）
 * - income：昨日收益汇总
 * - amount：金额（字符串）
 * - h24HashRate：过去24小时平均算力汇总
 */
public class AppUserDailySummaryItemVo {
    private String statDate;
    private BigDecimal income;
    private String amount;
    private BigDecimal h24HashRate;

    public String getStatDate() { return statDate; }
    public void setStatDate(String statDate) { this.statDate = statDate; }
    public BigDecimal getIncome() { return income; }
    public void setIncome(BigDecimal income) { this.income = income; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public BigDecimal getH24HashRate() { return h24HashRate; }
    public void setH24HashRate(BigDecimal h24HashRate) { this.h24HashRate = h24HashRate; }
}

