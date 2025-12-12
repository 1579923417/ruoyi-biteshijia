package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserEarningsDetailVo {
    private Long id;
    private String settleTime;
    private java.math.BigDecimal quantity;
    private java.math.BigDecimal amount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSettleTime() { return settleTime; }
    public void setSettleTime(String settleTime) { this.settleTime = settleTime; }
    public java.math.BigDecimal getQuantity() { return quantity; }
    public void setQuantity(java.math.BigDecimal quantity) { this.quantity = quantity; }
    public java.math.BigDecimal getAmount() { return amount; }
    public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
}
