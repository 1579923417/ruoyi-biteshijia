package com.ruoyi.system.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

public class AppUserMiner extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long brandId;
    private String brandName;
    private String apiCode;
    private BigDecimal managementFeeRate;
    private BigDecimal totalMined;
    private BigDecimal yesterdayMined;
    private BigDecimal todayMined;
    private BigDecimal totalIncome;
    private BigDecimal yesterdayIncome;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getApiCode() { return apiCode; }
    public void setApiCode(String apiCode) { this.apiCode = apiCode; }
    public BigDecimal getManagementFeeRate() { return managementFeeRate; }
    public void setManagementFeeRate(BigDecimal managementFeeRate) { this.managementFeeRate = managementFeeRate; }
    public BigDecimal getTotalMined() { return totalMined; }
    public void setTotalMined(BigDecimal totalMined) { this.totalMined = totalMined; }
    public BigDecimal getYesterdayMined() { return yesterdayMined; }
    public void setYesterdayMined(BigDecimal yesterdayMined) { this.yesterdayMined = yesterdayMined; }
    public BigDecimal getTodayMined() { return todayMined; }
    public void setTodayMined(BigDecimal todayMined) { this.todayMined = todayMined; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getYesterdayIncome() { return yesterdayIncome; }
    public void setYesterdayIncome(BigDecimal yesterdayIncome) { this.yesterdayIncome = yesterdayIncome; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
