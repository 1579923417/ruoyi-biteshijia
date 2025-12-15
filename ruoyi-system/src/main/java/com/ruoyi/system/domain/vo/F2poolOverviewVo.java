package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * F2Pool 概览返回对象
 *
 * 聚合 F2Pool 第三方接口的数据，用于前端展示：
 * - totalYesterdayIncome：所有子帐户昨日收益之和
 * - totalIncome：所有子帐户总收益之和
 * - totalTodayIncome：今日总收益（总收益 - 昨日收益）
 * - minerCount：子帐户数量（来自 /v2/mining_user/list 的 mining_user_name 数量）
 * - 用户基本信息：name、phone、bankName、bankAccount
 */
public class F2poolOverviewVo {
    private BigDecimal totalYesterdayIncome;
    private BigDecimal totalHashRate;
    private BigDecimal totalIncome;
    private BigDecimal totalTodayIncome;
    private Integer minerCount;
    private String name;
    private String phone;
    private String bankName;
    private String bankAccount;

    /**
     * 获取昨日收益总和
     */
    public BigDecimal getTotalYesterdayIncome() { return totalYesterdayIncome; }
    public void setTotalYesterdayIncome(BigDecimal v) { this.totalYesterdayIncome = v; }
    /**
     * 获取实时算力总和
     */
    public BigDecimal getTotalHashRate() { return totalHashRate; }
    public void setTotalHashRate(BigDecimal v) { this.totalHashRate = v; }
    /**
     * 获取总收益总和
     */
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal v) { this.totalIncome = v; }
    /**
     * 获取今日总收益
     */
    public BigDecimal getTotalTodayIncome() { return totalTodayIncome; }
    public void setTotalTodayIncome(BigDecimal v) { this.totalTodayIncome = v; }
    /**
     * 获取用户名称
     */
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    /**
     * 获取用户手机号
     */
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    /**
     * 获取开户行名称
     */
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    /**
     * 获取银行账号
     */
    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    /**
     * 获取子帐户数量
     */
    public Integer getMinerCount() { return minerCount; }
    public void setMinerCount(Integer minerCount) { this.minerCount = minerCount; }
}

