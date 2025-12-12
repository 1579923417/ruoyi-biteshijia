package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserEarningsVo {
    private BigDecimal totalMined;
    private BigDecimal yesterdayMined;
    private BigDecimal todayMined;
    private BigDecimal yesterdayIncomeAmount;
    private List<AppUserEarningsDetailVo> details;

    public BigDecimal getTotalMined() { return totalMined; }
    public void setTotalMined(BigDecimal totalMined) { this.totalMined = totalMined; }
    public BigDecimal getYesterdayMined() { return yesterdayMined; }
    public void setYesterdayMined(BigDecimal yesterdayMined) { this.yesterdayMined = yesterdayMined; }
    public BigDecimal getTodayMined() { return todayMined; }
    public void setTodayMined(BigDecimal todayMined) { this.todayMined = todayMined; }
    public BigDecimal getYesterdayIncomeAmount() { return yesterdayIncomeAmount; }
    public void setYesterdayIncomeAmount(BigDecimal yesterdayIncomeAmount) { this.yesterdayIncomeAmount = yesterdayIncomeAmount; }
    public List<AppUserEarningsDetailVo> getDetails() { return details; }
    public void setDetails(List<AppUserEarningsDetailVo> details) { this.details = details; }
}
