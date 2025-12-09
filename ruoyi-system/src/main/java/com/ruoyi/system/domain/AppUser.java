package com.ruoyi.system.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

public class AppUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String phone;
    private String bankName;
    private String bankAccount;
    private Integer minerCount;
    private BigDecimal totalIncome;
    private BigDecimal yesterdayIncome;
    private BigDecimal todayIncome;
    private Date updateTime;
    private String password;

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
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
