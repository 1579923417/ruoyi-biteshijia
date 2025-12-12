package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserMinerItemVo {
    private Long id;
    private String brandName;
    private String apiCode;
    private BigDecimal totalMined;
    private BigDecimal hashRate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getApiCode() { return apiCode; }
    public void setApiCode(String apiCode) { this.apiCode = apiCode; }
    public BigDecimal getTotalMined() { return totalMined; }
    public void setTotalMined(BigDecimal totalMined) { this.totalMined = totalMined; }
    public BigDecimal getHashRate() { return hashRate; }
    public void setHashRate(BigDecimal hashRate) { this.hashRate = hashRate; }
}
