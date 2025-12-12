package com.ruoyi.system.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 全球矿场信息实体
 */
public class MinerFarm extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String location;
    private String energyType;
    private BigDecimal priceWetSeason;
    private BigDecimal priceDrySeason;
    private BigDecimal priceAllYear;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getEnergyType() { return energyType; }
    public void setEnergyType(String energyType) { this.energyType = energyType; }
    public BigDecimal getPriceWetSeason() { return priceWetSeason; }
    public void setPriceWetSeason(BigDecimal priceWetSeason) { this.priceWetSeason = priceWetSeason; }
    public BigDecimal getPriceDrySeason() { return priceDrySeason; }
    public void setPriceDrySeason(BigDecimal priceDrySeason) { this.priceDrySeason = priceDrySeason; }
    public BigDecimal getPriceAllYear() { return priceAllYear; }
    public void setPriceAllYear(BigDecimal priceAllYear) { this.priceAllYear = priceAllYear; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}

