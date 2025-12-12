package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserMinerListVo {
    private BigDecimal totalHashRate;
    private List<AppUserMinerItemVo> items;

    public BigDecimal getTotalHashRate() { return totalHashRate; }
    public void setTotalHashRate(BigDecimal totalHashRate) { this.totalHashRate = totalHashRate; }
    public List<AppUserMinerItemVo> getItems() { return items; }
    public void setItems(List<AppUserMinerItemVo> items) { this.items = items; }
}
