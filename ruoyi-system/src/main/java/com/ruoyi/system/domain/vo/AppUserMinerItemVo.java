package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserMinerItemVo {
    private Long id;
    private String brandName;
    private String miningUserName;
    private String apiCode;
    private BigDecimal totalIncome;
    private BigDecimal hashRate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getMiningUserName() { return miningUserName; }
    public void setMiningUserName(String miningUserName) { this.miningUserName = miningUserName; }
    public String getApiCode() { return apiCode; }
    public void setApiCode(String apiCode) { this.apiCode = apiCode; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getHashRate() { return hashRate; }
    public void setHashRate(BigDecimal hashRate) { this.hashRate = hashRate; }
}
