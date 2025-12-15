package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

public class AppUserEarningItemDetailVo {
    private Long id;
    private String date;
    private BigDecimal quantity;
    private BigDecimal amount;
    private BigDecimal powerCost;
    private BigDecimal managementFee;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPowerCost() { return powerCost; }
    public void setPowerCost(BigDecimal powerCost) { this.powerCost = powerCost; }
    public BigDecimal getManagementFee() { return managementFee; }
    public void setManagementFee(BigDecimal managementFee) { this.managementFee = managementFee; }
}

