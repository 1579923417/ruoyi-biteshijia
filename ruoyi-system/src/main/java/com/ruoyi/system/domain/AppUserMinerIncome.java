package com.ruoyi.system.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户矿机每日收益记录实体
 */
public class AppUserMinerIncome extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long minerId;
    private Date date;
    private BigDecimal minedAmount;
    private BigDecimal incomeUsdt;
    private BigDecimal incomeRmb;
    private BigDecimal powerCost;
    private BigDecimal managementFee;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getMinerId() { return minerId; }
    public void setMinerId(Long minerId) { this.minerId = minerId; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public BigDecimal getMinedAmount() { return minedAmount; }
    public void setMinedAmount(BigDecimal minedAmount) { this.minedAmount = minedAmount; }
    public BigDecimal getIncomeUsdt() { return incomeUsdt; }
    public void setIncomeUsdt(BigDecimal incomeUsdt) { this.incomeUsdt = incomeUsdt; }
    public BigDecimal getIncomeRmb() { return incomeRmb; }
    public void setIncomeRmb(BigDecimal incomeRmb) { this.incomeRmb = incomeRmb; }
    public BigDecimal getPowerCost() { return powerCost; }
    public void setPowerCost(BigDecimal powerCost) { this.powerCost = powerCost; }
    public BigDecimal getManagementFee() { return managementFee; }
    public void setManagementFee(BigDecimal managementFee) { this.managementFee = managementFee; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}

