package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

public class AppUserMinerAddReq {
    private Long userId;
    private Long brandId;
    private String miningUserName;
    private String apiCode;
    private BigDecimal managementFeeRate;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public String getMiningUserName() { return miningUserName; }
    public void setMiningUserName(String miningUserName) { this.miningUserName = miningUserName; }
    public String getApiCode() { return apiCode; }
    public void setApiCode(String apiCode) { this.apiCode = apiCode; }
    public BigDecimal getManagementFeeRate() { return managementFeeRate; }
    public void setManagementFeeRate(BigDecimal managementFeeRate) { this.managementFeeRate = managementFeeRate; }
}

