package com.ruoyi.system.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

public class GlobalPowerInfo extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String country;
    private BigDecimal powerPriceCny;
    private String supplyPeriod;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public BigDecimal getPowerPriceCny() { return powerPriceCny; }
    public void setPowerPriceCny(BigDecimal powerPriceCny) { this.powerPriceCny = powerPriceCny; }
    public String getSupplyPeriod() { return supplyPeriod; }
    public void setSupplyPeriod(String supplyPeriod) { this.supplyPeriod = supplyPeriod; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
