package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

/**
 * F2Pool 子帐户列表项
 *
 * 对应第三方 F2Pool 的子帐户信息：
 * - miningUserName：子帐户名（mining_user_name）
 * - totalIncome：该子帐户的总收益（合并其所有钱包的 total_income）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class F2poolMinerItemVo {
    private String miningUserName;
    private BigDecimal totalIncome;

    /** 获取子帐户名 */
    public String getMiningUserName() { return miningUserName; }
    public void setMiningUserName(String miningUserName) { this.miningUserName = miningUserName; }
    /** 获取子帐户总收益 */
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
}
