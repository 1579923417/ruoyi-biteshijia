package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

public class MinerFarmVo {
    private Long id;
    private String name;
    private String location;
    private String energyType;
    private BigDecimal priceWetSeason;
    private BigDecimal priceDrySeason;
    private BigDecimal priceAllYear;
    private Integer status;

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
}

