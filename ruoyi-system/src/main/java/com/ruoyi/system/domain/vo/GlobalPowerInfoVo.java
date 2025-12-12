package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

public class GlobalPowerInfoVo {
    private Long id;
    private String name;
    private String country;
    private BigDecimal powerPriceCny;
    private String supplyPeriod;
    private Integer status;

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
}

