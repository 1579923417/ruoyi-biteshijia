package com.ruoyi.system.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

public class MinerBrand extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String brandName;
    private BigDecimal price;
    private BigDecimal hashRate;
    private BigDecimal powerPerHour;
    private BigDecimal dailyYield;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getHashRate() { return hashRate; }
    public void setHashRate(BigDecimal hashRate) { this.hashRate = hashRate; }
    public BigDecimal getPowerPerHour() { return powerPerHour; }
    public void setPowerPerHour(BigDecimal powerPerHour) { this.powerPerHour = powerPerHour; }
    public BigDecimal getDailyYield() { return dailyYield; }
    public void setDailyYield(BigDecimal dailyYield) { this.dailyYield = dailyYield; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
