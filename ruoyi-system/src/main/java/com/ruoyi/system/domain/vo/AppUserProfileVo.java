package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserProfileVo {
    private Long id;
    private String name;
    private String phone;
    private String avatar;
    private String bankName;
    private String bankAccount;
    private Integer minerCount;
    private BigDecimal totalIncome;
    private BigDecimal yesterdayIncome;
    private BigDecimal todayIncome;

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    public Integer getMinerCount() { return minerCount; }
    public void setMinerCount(Integer minerCount) { this.minerCount = minerCount; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getYesterdayIncome() { return yesterdayIncome; }
    public void setYesterdayIncome(BigDecimal yesterdayIncome) { this.yesterdayIncome = yesterdayIncome; }
    public BigDecimal getTodayIncome() { return todayIncome; }
    public void setTodayIncome(BigDecimal todayIncome) { this.todayIncome = todayIncome; }
}
